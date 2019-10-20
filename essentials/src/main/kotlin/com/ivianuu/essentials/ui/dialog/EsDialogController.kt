/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.dialog

import android.content.DialogInterface
import android.view.View
import com.ivianuu.director.common.DialogController
import com.ivianuu.essentials.injection.childControllerComponent
import com.ivianuu.essentials.injection.controllerComponent
import com.ivianuu.essentials.ui.mvrx.MvRxView
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.inject

/**
 * Base dialog controller
 */
abstract class EsDialogController : DialogController(), InjektTrait, MvRxView {

    override val component by unsafeLazy {
        if (parentController != null) {
            childControllerComponent {
                modules(this@EsDialogController.modules())
            }
        } else {
            controllerComponent {
                modules(this@EsDialogController.modules())
            }
        }
    }

    val navigator by inject<Navigator>()

    private var popped = false

    override fun onAttach(view: View) {
        super.onAttach(view)
        invalidate()
    }

    override fun invalidate() {
    }

    override fun onCancel(dialog: DialogInterface) {
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
    }

    override fun dismiss() {
        dismiss(null)
    }

    fun dismiss(result: Any?) {
        if (!popped) {
            popped = true
            dialog?.dismiss()
            navigator.pop(result)
        }
    }

    protected open fun modules(): List<Module> = emptyList()

}