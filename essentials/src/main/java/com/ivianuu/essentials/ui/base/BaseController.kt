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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.director.Controller
import com.ivianuu.director.arch.lifecycle.ControllerLifecycleOwner
import com.ivianuu.director.arch.lifecycle.LifecycleController
import com.ivianuu.director.common.contextRef
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.injection.director.DirectorInjection
import com.ivianuu.essentials.injection.director.HasControllerInjector
import com.ivianuu.essentials.injection.view.HasViewInjector
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.traveler.RouterHolder
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.ViewInjectionContextWrapper
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.essentials.util.lifecycle.LifecycleCoroutineScope
import com.ivianuu.essentials.util.lifecycle.LifecycleJob
import com.ivianuu.essentials.util.lifecycle.LifecycleOwner2
import com.ivianuu.essentials.util.screenlogger.IdentifiableScreen
import com.ivianuu.essentials.util.viewmodel.ViewModelFactoryHolder
import com.ivianuu.rxlifecycle.RxLifecycleOwner
import com.ivianuu.traveler.Router
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Base fragment
 */
abstract class BaseController : LifecycleController(), ContextAware, CoroutineScope, HasControllerInjector,
    HasViewInjector, Injectable, IdentifiableScreen, LayoutContainer,
    LifecycleOwner2, MvRxView, RouterHolder, RxLifecycleOwner, ViewModelFactoryHolder {

    @set:Inject var travelerRouter: Router by contextRef()
    @set:Inject var controllerInjector: DispatchingAndroidInjector<Controller> by contextRef()
    @set:Inject var viewInjector: DispatchingAndroidInjector<View> by contextRef()
    @set:Inject override var viewModelFactory: ViewModelProvider.Factory by contextRef()

    override var containerView: View? = null

    override val providedContext: Context
        get() = requireActivity()

    override val providedRouter: Router
        get() = travelerRouter

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val job by unsafeLazy { LifecycleJob(this) }

    val viewCoroutineScope: CoroutineScope
        get() = _viewCoroutineScope ?: throw IllegalArgumentException("view == null")
    private var _viewCoroutineScope: LifecycleCoroutineScope? = null

    protected open val layoutRes = -1

    override fun onContextAvailable(context: Context) {
        DirectorInjection.inject(this)
        super.onContextAvailable(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View = if (layoutRes != -1) {
        val viewInjectionContext =
            ViewInjectionContextWrapper(requireActivity(), this)
        val viewInjectionInflater = inflater.cloneInContext(viewInjectionContext)
        val view = viewInjectionInflater.inflate(layoutRes, container, false)
            .also { containerView = it }

        onViewCreated(view)
        view
    } else {
        throw IllegalStateException("no layout res provided")
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        postInvalidate()
    }

    override fun onDestroyView(view: View) {
        containerView = null
        clearFindViewByIdCache()
        _viewCoroutineScope = null
        super.onDestroyView(view)
    }

    override fun invalidate() {
    }

    override fun controllerInjector(): AndroidInjector<Controller> = controllerInjector

    override fun viewInjector(): AndroidInjector<View> = viewInjector

    protected open fun onViewCreated(view: View) {
        _viewCoroutineScope = LifecycleCoroutineScope(viewLifecycleOwner)
    }
}