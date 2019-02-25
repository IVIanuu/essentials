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
import android.view.View
import androidx.lifecycle.Lifecycle
import com.ivianuu.director.androidx.lifecycle.lifecycleOwner
import com.ivianuu.director.context
import com.ivianuu.director.dialog.DialogController
import com.ivianuu.essentials.injection.controllerComponent
import com.ivianuu.essentials.ui.common.layoutContainer
import com.ivianuu.essentials.ui.mvrx.injekt.InjektMvRxView
import com.ivianuu.essentials.ui.traveler.key.keyModule
import com.ivianuu.essentials.ui.viewmodel.ViewModelManager
import com.ivianuu.essentials.ui.viewmodel.director.viewModelManagerOwner
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.inject
import com.ivianuu.injekt.modules
import com.ivianuu.traveler.Router
import kotlinx.android.extensions.LayoutContainer

/**
 * Base dialog controller
 */
abstract class EsDialogController : DialogController(),
    ContextAware, InjektMvRxView, LayoutContainer {

    override val component by unsafeLazy {
        controllerComponent {
            modules(keyModule(args))
            modules(this@EsDialogController.modules())
        }
    }

    override val containerView: View?
        get() = layoutContainer.containerView

    override val providedContext: Context
        get() = context

    val travelerRouter by inject<Router>()

    override val viewModelManager: ViewModelManager
        get() = viewModelManagerOwner.viewModelManager

    init {
        viewModelManagerOwner // todo remove this
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        invalidate()
    }

    override fun invalidate() {
    }

    override fun getLifecycle(): Lifecycle = lifecycleOwner.lifecycle

    protected open fun modules(): List<Module> = emptyList()
}