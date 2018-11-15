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
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.base.BaseDialogFragment
import com.ivianuu.essentials.ui.traveler.key.BaseFragmentKey
import com.ivianuu.essentials.ui.traveler.key.ResultKey
import com.ivianuu.essentials.ui.traveler.key.key
import com.ivianuu.essentials.util.RequestCodeGenerator
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
) : BaseFragmentKey(TextInputDialog::class), ResultKey<CharSequence>

/**
 * Text input dialog
 */
class TextInputDialog : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val key = key<TextInputKey>()

        return MaterialDialog.Builder(requireContext())
            .autoDismiss(false)
            .title(key.title)
            .input(
                key.inputHint,
                key.prefill,
                key.allowEmptyInput
            ) { _, input ->
                router.goBackWithResult(key.resultCode, input)
            }
            .onNegative { _, _ -> router.goBack() }
            .inputType(key.inputType)
            .positiveText(R.string.action_ok)
            .negativeText(R.string.action_cancel)
            .build()
    }

}