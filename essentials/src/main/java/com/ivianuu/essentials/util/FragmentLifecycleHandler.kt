/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.ivianuu.daggerextensions.AutoBindsIntoSet
import com.ivianuu.essentials.injection.EssentialsServiceModule
import com.ivianuu.essentials.internal.EssentialsService
import com.ivianuu.essentials.ui.common.FragmentEvent
import com.ivianuu.essentials.ui.compat.ViewLifecycleDialogFragment
import com.ivianuu.essentials.ui.compat.ViewLifecycleFragment
import com.ivianuu.essentials.ui.compat.ViewLifecyclePreferenceFragment
import com.ivianuu.essentials.util.ext.behaviorSubject
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Handles the lifecycle of all fragments
 */
// todo this can never wokr
@EssentialsServiceModule
@AutoBindsIntoSet(EssentialsService::class)
class FragmentLifecycleHandler @Inject constructor(
    application: Application
) : EssentialsService {

    private val activityLifecycleCallbacks = object : SimpleActivityLifecycleCallbacks() {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            // add our fragment lifecycle callbacks
            if (activity is FragmentActivity) {
                activity.supportFragmentManager
                    .registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
            }
        }
    }

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
            if (f is AdditionalLifecycleFragment) return // avoid loops

            // this is the first time we get in touch with the fragment
            // so create a lifecycle and dispatch attach
            val lifecycle = behaviorSubject<FragmentEvent>()
            lifecycles[f] = lifecycle
            lifecycle.onNext(FragmentEvent.ATTACH)

            // add our observer
            f.lifecycle.addObserver(fragmentLifecycleObserver)

            // add the additional lifecycle fragment
            AdditionalLifecycleFragment.install(f)

            // add a view observer
            val viewLifecycleOwner = when(f) {
                is ViewLifecycleDialogFragment -> f.viewLifecycleOwner
                is ViewLifecycleFragment -> f.viewLifecycleOwner
                is ViewLifecyclePreferenceFragment -> f.viewLifecycleOwner
                else -> null
            }

            if (viewLifecycleOwner != null) {
                val viewObserver = FragmentViewLifecycleObserver(f)
                viewLifecycleObservers[f] = viewObserver
                viewLifecycleOwner.lifecycle.addObserver(viewObserver)
            }
        }

        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            if (f is AdditionalLifecycleFragment) return // avoid loops

            // clean up to avoid leaks
            // the actual detach event was already dispatched by the AdditionalLifecycleFragment
            lifecycles.remove(f)
            f.lifecycle.removeObserver(fragmentLifecycleObserver)

            // remove view observer
            val viewObserver = viewLifecycleObservers.remove(f)
            if (viewObserver != null) {
                val viewLifecycleOwner = when(f) {
                    is ViewLifecycleDialogFragment -> f.viewLifecycleOwner
                    is ViewLifecycleFragment -> f.viewLifecycleOwner
                    is ViewLifecyclePreferenceFragment -> f.viewLifecycleOwner
                    else -> null
                }

                viewLifecycleOwner?.lifecycle?.removeObserver(viewObserver)
            }
        }
    }

    private val fragmentLifecycleObserver = object : SimpleLifecycleObserver() {

        override fun onCreate(owner: LifecycleOwner) {
            val fragment = owner as? Fragment
            val lifecycle = lifecycles[fragment]
            if (fragment != null
                && lifecycle != null
                && lifecycle.value != FragmentEvent.CREATE) {
                lifecycle.onNext(FragmentEvent.CREATE)
            }
        }

        override fun onStart(owner: LifecycleOwner) {
            val fragment = owner as? Fragment
            if (fragment != null) {
                lifecycles[fragment]?.onNext(FragmentEvent.START)
            }
        }

        override fun onResume(owner: LifecycleOwner) {
            val fragment = owner as? Fragment
            if (fragment != null) {
                lifecycles[fragment]?.onNext(FragmentEvent.RESUME)
            }
        }

        override fun onPause(owner: LifecycleOwner) {
            val fragment = owner as? Fragment
            if (fragment != null) {
                lifecycles[fragment]?.onNext(FragmentEvent.PAUSE)
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            val fragment = owner as? Fragment
            if (fragment != null) {
                lifecycles[fragment]?.onNext(FragmentEvent.STOP)
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            val fragment = owner as? Fragment
            if (fragment != null) {
                lifecycles[fragment]?.onNext(FragmentEvent.DESTROY)
            }
        }
    }

    init {
        INSTANCE = this
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun onFragmentCreateView(fragment: Fragment) {
        lifecycles[fragment]?.onNext(FragmentEvent.CREATE_VIEW)
    }

    fun onFragmentDestroyView(fragment: Fragment) {
        lifecycles[fragment]?.onNext(FragmentEvent.DESTROY_VIEW)
    }

    fun onFragmentPreDetached(fragment: Fragment) {
        lifecycles[fragment]?.onNext(FragmentEvent.DETACH)
    }

    /**
     * This fragment will be injected as child fragment and allows to get additional
     * lifecycle events which are required e.g. pre detach
     * this works because the parent/child relationship works in a last in/first out manner
     */
    class AdditionalLifecycleFragment : Fragment() {

        override fun onDetach() {
            super.onDetach()
            FragmentLifecycleHandler.INSTANCE?.onFragmentPreDetached(parentFragment!!)
        }

        companion object {
            private const val FRAGMENT_TAG = "AdditionalLifecycleFragment"
            fun install(fragment: Fragment): AdditionalLifecycleFragment {
                var additionalLifecycleFragment =
                    fragment.childFragmentManager.findFragmentByTag(FRAGMENT_TAG) as AdditionalLifecycleFragment?

                if (additionalLifecycleFragment == null) {
                    additionalLifecycleFragment = AdditionalLifecycleFragment()
                    fragment.childFragmentManager.beginTransaction()
                        .add(additionalLifecycleFragment, FRAGMENT_TAG)
                        .commitNow()
                }

                return additionalLifecycleFragment
            }
        }
    }

    private class FragmentViewLifecycleObserver(private val fragment: Fragment) : SimpleLifecycleObserver() {
        override fun onCreate(owner: LifecycleOwner) {
            FragmentLifecycleHandler.INSTANCE?.onFragmentCreateView(fragment)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            FragmentLifecycleHandler.INSTANCE?.onFragmentDestroyView(fragment)
        }
    }

    companion object {
        private var INSTANCE: FragmentLifecycleHandler? = null

        private val lifecycles =
            mutableMapOf<Fragment, BehaviorSubject<FragmentEvent>>()
        private val viewLifecycleObservers =
                mutableMapOf<Fragment, FragmentViewLifecycleObserver>()

        fun getLifecycle(fragment: Fragment): Observable<FragmentEvent> {
            return lifecycles[fragment]?.distinctUntilChanged() ?: Observable.never()
        }

        fun peekLifecycle(fragment: Fragment): FragmentEvent? {
            return lifecycles[fragment]?.value
        }

        fun backfillEvents(fragment: Fragment) {
            val lifecycleSubject = lifecycles[fragment] ?: return

            val viewLifecycle = when (fragment) {
                is ViewLifecycleDialogFragment -> fragment.viewLifecycleOwner
                is ViewLifecycleFragment -> fragment.viewLifecycleOwner
                is ViewLifecyclePreferenceFragment -> fragment.viewLifecycleOwner
                else -> null
            }?.lifecycle

            val viewLifecycleState = viewLifecycle?.currentState

            val lifecycle = fragment.lifecycle
            val lifecycleState = lifecycle.currentState

            val event = if (viewLifecycleState != null) {
                when (viewLifecycleState) {
                    Lifecycle.State.INITIALIZED -> {
                        if (lifecycleState == Lifecycle.State.INITIALIZED) {
                            FragmentEvent.CREATE
                        } else {
                            null
                        }
                    }
                    Lifecycle.State.CREATED -> FragmentEvent.CREATE_VIEW
                    Lifecycle.State.STARTED -> FragmentEvent.START
                    Lifecycle.State.RESUMED -> FragmentEvent.RESUME
                    Lifecycle.State.DESTROYED -> {
                        if (lifecycleState == Lifecycle.State.DESTROYED) {
                            FragmentEvent.DESTROY
                        } else {
                            FragmentEvent.DESTROY_VIEW
                        }
                    }
                    else -> {
                        when (lifecycleState) {
                            Lifecycle.State.INITIALIZED -> FragmentEvent.CREATE
                            Lifecycle.State.CREATED -> FragmentEvent.START
                            Lifecycle.State.STARTED, Lifecycle.State.RESUMED -> FragmentEvent.RESUME
                            Lifecycle.State.DESTROYED -> {
                                if (fragment.isAdded) {
                                    FragmentEvent.DESTROY
                                } else {
                                    FragmentEvent.DETACH
                                }
                            }
                            else -> FragmentEvent.DETACH
                        }
                    }
                }
            } else {
                null
            }

            if (event != null) {
                lifecycleSubject.onNext(event)
            }
        }
    }
}