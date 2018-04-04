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

package com.ivianuu.essentials.ui.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.view.doOnPreDraw
import androidx.view.postDelayed
import com.ivianuu.essentials.ui.common.BackListener
import com.ivianuu.traveler.Router
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Base fragment
 */
abstract class BaseFragment : Fragment(), BackListener, HasSupportFragmentInjector {

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject lateinit var router: Router

    protected val disposables = CompositeDisposable()

    protected open val layoutRes = -1

    protected open val sharedElementMaxDelay = 500L

    private var startedTransition = false
    private var postponed = false

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        activity.let {
            if (it is BaseActivity) {
                it.addBackListener(this)
            }
        }

        setupTransitions(TransitionInflater.from(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (layoutRes != -1) {
            inflater.inflate(layoutRes, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onStart() {
        super.onStart()

        if (postponed && !startedTransition) {
            // If we're postponed and haven't started a transition yet, we'll delay for a max of [sharedElementDelay]ms
            view?.postDelayed(sharedElementMaxDelay, this::scheduleStartPostponedTransitions)
        }
    }

    override fun onStop() {
        super.onStop()
        startedTransition = false
    }

    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
    }

    override fun onDestroy() {
        val activity = activity
        if (activity != null
            && activity.isFinishing
            && !activity.isChangingConfigurations) {
            viewModelStore.clear()
        }
        super.onDestroy()
    }

    override fun onDetach() {
        activity.let {
            if (it is BaseActivity) {
                it.removeBackListener(this)
            }
        }
        super.onDetach()
    }

    override fun postponeEnterTransition() {
        super.postponeEnterTransition()
        postponed = true
    }

    override fun handleBack(): Boolean {
        return false
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    protected fun scheduleStartPostponedTransitions() {
        if (!startedTransition) {
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
            startedTransition = true
        }
    }

    protected open fun setupTransitions(inflater: TransitionInflater) {
    }
}