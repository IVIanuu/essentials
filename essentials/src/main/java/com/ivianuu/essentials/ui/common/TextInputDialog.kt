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

package com.ivianuu.essentials.ui.common

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.base.BaseDialogFragment
import com.ivianuu.essentials.ui.traveler.key.FragmentClassKey
import com.ivianuu.essentials.ui.traveler.key.ResultKey
import com.ivianuu.essentials.ui.traveler.key.requireKey
import com.ivianuu.essentials.ui.traveler.router
import kotlinx.android.parcel.Parcelize

/**
 * Text input dialog
 */
class TextInputDialog : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val key = requireKey<TextInputKey>()

        return MaterialDialog.Builder(requireContext())
            .title(key.title)
            .input(key.inputHint, key.prefill, key.allowEmptyInput,
                { _, input -> router.sendResult(key.resultCode, input.toString()) }
            )
            .inputType(key.inputType)
            .positiveText(R.string.action_ok)
            .negativeText(R.string.action_cancel)
            .build()
    }

}

/**
 * Key for the [TextInputDialog]
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class TextInputKey(
    override var resultCode: Int,
    val title: String,
    val inputHint: String = "",
    val inputType: Int = 0,
    val prefill: String = "",
    val allowEmptyInput: Boolean = false
) : FragmentClassKey(TextInputDialog::class), ResultKey, Parcelable