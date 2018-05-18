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

package com.ivianuu.essentials.ui.traveler

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.NavigatorHolder

/**
 * A [LifecycleObserver] which automatically sets and removes the navigator on the corresponding lifecycle events
 */
class NavigatorLifecycleObserver(
    lifecycleOwner: LifecycleOwner,
    private val navigator: Navigator,
    private val navigatorHolder: NavigatorHolder
) : LifecycleObserver {

    init {
        // if its a activity we must add a dummy fragment to make sure
        // that we do NOT set the navigator before onResumeFragments was called
        // otherwise this would lead to crashes
        val lifecycle = if (lifecycleOwner is FragmentActivity) {
            LifecycleProviderFragment.get(lifecycleOwner).lifecycle
        } else {
            lifecycleOwner.lifecycle
        }

        // observe
        lifecycle.addObserver(this)

        // update navigator state based on the current lifecycle state
        updateState(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        d { "on resume" }
        updateState(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        d { "on pause" }
        updateState(false)
    }

    private fun updateState(shouldAttach: Boolean) {
        d { "update state $shouldAttach" }

        if (shouldAttach) {
            navigatorHolder.setNavigator(navigator)
        } else {
            navigatorHolder.removeNavigator()
        }
    }

    /**
     * Used for activities because their on resume is called to early
     */
    class LifecycleProviderFragment : Fragment() {

        companion object {
            private const val FRAGMENT_TAG = "LifecycleProviderFragment"

            fun get(activity: FragmentActivity): LifecycleProviderFragment {
                var fragment =
                    activity.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
                            as LifecycleProviderFragment?

                if (fragment == null) {
                    fragment = LifecycleProviderFragment()

                    activity.supportFragmentManager.beginTransaction()
                        .add(fragment, FRAGMENT_TAG)
                        .commitNow()
                }

                return fragment
            }
        }

    }
}