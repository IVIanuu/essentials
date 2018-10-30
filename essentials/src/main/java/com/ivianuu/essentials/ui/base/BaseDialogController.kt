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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.contributor.director.DirectorInjection
import com.ivianuu.director.arch.lifecycle.ControllerLifecycleOwner
import com.ivianuu.director.arch.lifecycle.ControllerViewModelStoreOwner
import com.ivianuu.director.common.contextRef
import com.ivianuu.director.dialog.DialogController
import com.ivianuu.director.requireActivity
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.ui.traveler.RouterHolder
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.lifecycle.LifecycleOwner2
import com.ivianuu.essentials.util.screenlogger.IdentifiableScreen
import com.ivianuu.essentials.util.viewmodel.ViewModelFactoryHolder
import com.ivianuu.traveler.Router
import javax.inject.Inject

/**
 * Base dialog controller
 */
abstract class BaseDialogController : DialogController(),
    ContextAware, Injectable, IdentifiableScreen,
    LifecycleOwner, LifecycleOwner2, RouterHolder, ViewModelFactoryHolder,
    ViewModelStoreOwner {

    @set:Inject var travelerRouter: Router by contextRef()
    @set:Inject override var viewModelFactory: ViewModelProvider.Factory by contextRef()

    override val providedContext: Context
        get() = requireActivity()

    override val providedRouter: Router
        get() = travelerRouter

    private val lifecycleOwner = ControllerLifecycleOwner()
    private val viewModelStoreOwner = ControllerViewModelStoreOwner()

    override fun onContextAvailable(context: Context) {
        DirectorInjection.inject(this)
        super.onContextAvailable(context)
    }

    override fun getLifecycle() = lifecycleOwner.lifecycle

    override fun getViewModelStore() = viewModelStoreOwner.viewModelStore
}