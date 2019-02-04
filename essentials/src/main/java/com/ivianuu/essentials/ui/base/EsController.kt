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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.director.Controller
import com.ivianuu.director.arch.lifecycle.ControllerLifecycleOwner
import com.ivianuu.director.arch.lifecycle.ControllerViewModelStoreOwner
import com.ivianuu.director.scopes.destroy
import com.ivianuu.director.scopes.unbindView
import com.ivianuu.essentials.injection.controllerComponent
import com.ivianuu.essentials.ui.mvrx.injekt.InjektMvRxView
import com.ivianuu.essentials.ui.traveler.key.keyModule
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.InjektTraitContextWrapper
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.inject
import com.ivianuu.injekt.modules
import com.ivianuu.traveler.Router
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*
import kotlinx.coroutines.CoroutineScope

/**
 * Base controller
 */
abstract class EsController : Controller(), ContextAware,
    InjektMvRxView, LayoutContainer,
    LifecycleOwner, ViewModelStoreOwner {

    override val component by unsafeLazy {
        controllerComponent(this) {
            modules(keyModule(args))
            modules(this@EsController.modules())
        }
    }

    val travelerRouter by inject<Router>()

    override val containerView: View?
        get() = view

    override val providedContext: Context
        get() = activity

    val coroutineScope = destroy.asMainCoroutineScope()

    private val lifecycleOwner = ControllerLifecycleOwner()
    private val viewModelStoreOwner =
        ControllerViewModelStoreOwner()

    val viewCoroutineScope
        get() = _viewCoroutineScope ?: error("view not attached")
    private var _viewCoroutineScope: CoroutineScope? = null

    protected open val layoutRes get() = -1

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        check(layoutRes != -1) { "no layoutRes provided" }

        val injectorInflater =
            inflater.cloneInContext(InjektTraitContextWrapper(activity, this))
        return injectorInflater.inflate(layoutRes, container, false)
    }

    override fun onBindView(view: View, savedViewState: Bundle?) {
        super.onBindView(view, savedViewState)
        _viewCoroutineScope = unbindView.asMainCoroutineScope()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        invalidate()
    }

    override fun onUnbindView(view: View) {
        _viewCoroutineScope = null
        clearFindViewByIdCache()
        super.onUnbindView(view)
    }

    override fun invalidate() {
    }

    override fun getLifecycle(): Lifecycle = lifecycleOwner.lifecycle

    override fun getViewModelStore(): ViewModelStore = viewModelStoreOwner.viewModelStore

    protected open fun modules(): List<Module> = emptyList()

}