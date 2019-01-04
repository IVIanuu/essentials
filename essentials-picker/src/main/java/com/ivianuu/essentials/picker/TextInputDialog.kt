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

package com.ivianuu.essentials.picker

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.base.EsDialogController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.injekt.get
import com.ivianuu.traveler.goBack
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TextInputKey(
    val title: String,
    val inputHint: String = "",
    val inputType: Int = -1,
    val prefill: String = "",
    val allowEmptyInput: Boolean = false,
    val resultCode: Int
) : ControllerKey(::TextInputDialog, NavOptions().dialog())

/**
 * Text input dialog
 */
class TextInputDialog : EsDialogController() {

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        val key = get<TextInputKey>()

        return MaterialDialog(activity)
            .noAutoDismiss()
            .title(text = key.title)
            .input(
                hint = key.inputHint,
                prefill = key.prefill,
                inputType = key.inputType
            ) { _, input ->
                travelerRouter.goBackWithResult(key.resultCode, input.toString())
            }
            .positiveButton(R.string.es_action_ok)
            .negativeButton(R.string.es_action_cancel) { travelerRouter.goBack() }
    }

}