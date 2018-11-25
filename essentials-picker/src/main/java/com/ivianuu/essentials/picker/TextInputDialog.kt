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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.base.BaseController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.ResultKey
import com.ivianuu.essentials.ui.traveler.key.key
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.materialdialogs.MaterialDialog
import com.ivianuu.materialdialogs.callback.onDismiss
import com.ivianuu.materialdialogs.input.input
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.result.goBackWithResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TextInputKey(
    val title: String,
    val inputHint: String = "",
    val inputType: Int = -1,
    val prefill: String = "",
    val allowEmptyInput: Boolean = false,
    override var resultCode: Int = RequestCodeGenerator.generate()
) : ControllerKey(TextInputDialog::class, NavOptions().dialog()), ResultKey<String>

/**
 * Text input dialog
 */
class TextInputDialog : BaseController() {

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val key = key<TextInputKey>()

        return MaterialDialog(activity)
            .autoDismiss(false)
            .title(text = key.title)
            .onDismiss { travelerRouter.goBack() }
            .input(
                hint = key.inputHint,
                prefill = key.prefill,
                inputType = key.inputType
            ) { _, input ->
                travelerRouter.goBackWithResult(key.resultCode, input.toString())
            }
            .positiveButton(R.string.action_ok)
            .negativeButton(R.string.action_cancel) { travelerRouter.goBack() }
            .view
    }

}