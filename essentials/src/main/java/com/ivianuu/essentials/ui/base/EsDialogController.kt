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
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import com.ivianuu.director.arch.lifecycle.ControllerLifecycleOwner
import com.ivianuu.director.arch.lifecycle.ControllerViewModelStoreOwner
import com.ivianuu.director.dialog.DialogController
import com.ivianuu.director.scopes.destroy
import com.ivianuu.essentials.injection.bindInstanceModule
import com.ivianuu.essentials.injection.componentName
import com.ivianuu.essentials.injection.getComponentDependencies
import com.ivianuu.essentials.injection.lazyComponent
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.injekt.*
import com.ivianuu.traveler.Router

/**
 * Base dialog controller
 */
abstract class EsDialogController : DialogController(), ComponentHolder, ContextAware, MvRxView {

    override val component by lazyComponent(componentName()) {
        dependencies(this@EsDialogController.dependencies())
        modules(implicitModules() + this@EsDialogController.modules())
    }

    val travelerRouter by inject<Router>()

    override val providedContext: Context
        get() = activity

    private val lifecycleOwner = ControllerLifecycleOwner()
    private val viewModelStoreOwner = ControllerViewModelStoreOwner()

    val coroutineScope = destroy.asMainCoroutineScope()

    override fun onRestoreViewState(view: View, savedViewState: Bundle) {
        super.onRestoreViewState(view, savedViewState)
        invalidate()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        invalidate()
    }

    override fun invalidate() {
    }

    override fun getLifecycle(): Lifecycle = lifecycleOwner.lifecycle

    override fun getViewModelStore(): ViewModelStore = viewModelStoreOwner.viewModelStore

    protected open fun dependencies() = getComponentDependencies()

    protected open fun modules() = emptyList<Module>()

    protected open fun implicitModules() = listOf(bindInstanceModule(this))
}