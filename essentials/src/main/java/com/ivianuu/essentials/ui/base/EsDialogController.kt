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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.director.androidx.lifecycle.lifecycleOwner
import com.ivianuu.director.androidx.lifecycle.viewModelStoreOwner
import com.ivianuu.director.context
import com.ivianuu.director.dialog.DialogController
import com.ivianuu.essentials.injection.controllerComponent
import com.ivianuu.essentials.ui.traveler.key.keyModule
import com.ivianuu.essentials.util.ContextAware

import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.inject

import com.ivianuu.stdlibx.unsafeLazy
import com.ivianuu.traveler.Router

/**
 * Base dialog controller
 */
abstract class EsDialogController : DialogController(), ContextAware, InjektTrait, LifecycleOwner,
    ViewModelStoreOwner {

    override val component by unsafeLazy {
        controllerComponent(
            modules = listOf(keyModule(args)) + modules()
        )
    }

    override val providedContext: Context
        get() = context

    override fun getViewModelStore(): ViewModelStore = viewModelStoreOwner.viewModelStore

    val travelerRouter by inject<Router>()

    override fun getLifecycle(): Lifecycle = lifecycleOwner.lifecycle

    protected open fun modules(): List<Module> = emptyList()
}