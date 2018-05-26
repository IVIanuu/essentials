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
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.autodispose.LifecycleScopeProvider
import com.ivianuu.daggerextensions.view.HasViewInjector
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.ui.common.CORRESPONDING_FRAGMENT_EVENTS
import com.ivianuu.essentials.ui.common.FragmentEvent
import com.ivianuu.essentials.ui.common.FragmentEvent.*
import com.ivianuu.essentials.ui.common.back.BackListener
import com.ivianuu.essentials.util.ViewInjectionContextWrapper
import com.ivianuu.essentials.util.analytics.NamedScreen
import com.ivianuu.essentials.util.ext.behaviorSubject
import com.ivianuu.traveler.Router
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Base dialog fragment
 */
abstract class BaseDialogFragment : DialogFragment(),
    BackListener, HasViewInjector, HasSupportFragmentInjector,
    Injectable, NamedScreen, LifecycleScopeProvider<FragmentEvent> {

    @Inject lateinit var router: Router

    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>

    protected open val layoutRes = -1

    private val lifecycleSubject = behaviorSubject<FragmentEvent>()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        lifecycleSubject.onNext(ATTACH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleSubject.onNext(CREATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (layoutRes != -1) {
            val viewInjectionContext =
                ViewInjectionContextWrapper(requireContext(), this)
            val viewInjectionInflater = inflater.cloneInContext(viewInjectionContext)
            viewInjectionInflater.inflate(layoutRes, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleSubject.onNext(CREATE_VIEW)
    }

    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(RESUME)
    }

    override fun onPause() {
        lifecycleSubject.onNext(PAUSE)
        super.onPause()
    }

    override fun onStop() {
        lifecycleSubject.onNext(STOP)
        super.onStop()
    }

    override fun onDestroyView() {
        lifecycleSubject.onNext(DESTROY_VIEW)
        super.onDestroyView()
    }

    override fun onDestroy() {
        lifecycleSubject.onNext(DESTROY)
        super.onDestroy()
    }

    override fun onDetach() {
        lifecycleSubject.onNext(DETACH)
        super.onDetach()
    }

    override fun handleBack(): Boolean {
        return false
    }

    override fun supportFragmentInjector() = supportFragmentInjector

    override fun viewInjector() = viewInjector

    override fun lifecycle() = lifecycleSubject

    override fun correspondingEvents() = CORRESPONDING_FRAGMENT_EVENTS

    override fun peekLifecycle() = lifecycleSubject.value

}