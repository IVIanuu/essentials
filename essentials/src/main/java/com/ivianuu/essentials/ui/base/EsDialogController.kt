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
import com.ivianuu.director.common.DialogController
import com.ivianuu.essentials.injection.controllerComponent
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.traveler.key.keyModule
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.inject
import com.ivianuu.traveler.Router

/**
 * Base dialog controller
 */
abstract class EsDialogController : DialogController(), ContextAware, InjektTrait,
    MvRxView {

    override val component by unsafeLazy {
        controllerComponent {
            modules(keyModule(args))
            modules(this@EsDialogController.modules())
        }
    }

    override val providedContext: Context
        get() = activity

    val travelerRouter by inject<Router>()

    override fun onAttach(view: View) {
        super.onAttach(view)
        invalidate()
    }

    override fun invalidate() {
    }

    protected open fun modules(): List<Module> = emptyList()

}