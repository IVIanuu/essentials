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
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.contributor.view.HasViewInjector
import com.ivianuu.director.Controller
import com.ivianuu.director.arch.lifecycle.LifecycleController
import com.ivianuu.director.contributor.DirectorInjection
import com.ivianuu.director.contributor.HasControllerInjector
import com.ivianuu.director.scopes.unbindView
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.ViewInjectionContextWrapper
import com.ivianuu.essentials.util.ViewModelFactoryHolder
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.scopes.archlifecycle.onDestroy
import com.ivianuu.traveler.Router
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * Base controller
 */
abstract class BaseController : LifecycleController(), ContextAware, HasControllerInjector,
    HasViewInjector,
    LayoutContainer, MvRxView, ViewModelFactoryHolder {

    @Inject lateinit var travelerRouter: Router

    @Inject lateinit var controllerInjector: DispatchingAndroidInjector<Controller>
    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>

    @Inject override lateinit var viewModelFactory: ViewModelProvider.Factory

    override val containerView: View?
        get() = view

    override val providedContext: Context
        get() = activity

    val coroutineScope = onDestroy.asMainCoroutineScope()

    val viewCoroutineScope
        get() = _viewCoroutineScope ?: throw IllegalStateException("view not attached")
    private var _viewCoroutineScope: CoroutineScope? = null

    protected open val layoutRes = -1

    override fun onCreate() {
        DirectorInjection.inject(this)
        super.onCreate()
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View = if (layoutRes != -1) {
        val injectorInflater =
            inflater.cloneInContext(ViewInjectionContextWrapper(activity, this))
        injectorInflater.inflate(layoutRes, container, false)
    } else {
        throw IllegalStateException("no layoutRes provided")
    }

    override fun onBindView(view: View) {
        super.onBindView(view)
        _viewCoroutineScope = unbindView.asMainCoroutineScope()
        invalidate()
    }

    override fun onUnbindView(view: View) {
        _viewCoroutineScope = null
        clearFindViewByIdCache()
        super.onUnbindView(view)
    }

    override fun invalidate() {
    }

    override fun controllerInjector(): AndroidInjector<Controller> = controllerInjector

    override fun viewInjector(): AndroidInjector<View> = viewInjector
}