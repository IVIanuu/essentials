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

package com.ivianuu.essentials.sample.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.essentials.ui.base.BaseDialogController
import com.ivianuu.essentials.ui.traveler.DialogControllerClassKey
import com.ivianuu.essentials.util.ext.d

/**
 * @author Manuel Wrage (IVIanuu)
 */
class SomeDialogController : BaseDialogController() {

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        return MaterialDialog.Builder(requireActivity())
            .title("Hello")
            .content("I'm a dialog")
            .positiveText("Dismiss")
            .build()
    }

    override fun onDestroyView(view: View) {
        d { "on destroy view" }
        super.onDestroyView(view)
    }
}

object SomeDialogKey : DialogControllerClassKey(SomeDialogController::class)