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
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.traveler.FragmentKey
import com.ivianuu.essentials.ui.traveler.ResultKey
import com.ivianuu.essentials.ui.traveler.requireKey
import com.ivianuu.essentials.ui.traveler.router
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.parcel.Parcelize

/**
 * Text input dialog
 */
class TextInputDialog : DialogFragment() {

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

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
) : FragmentKey(), ResultKey, Parcelable {
    override fun createFragment(data: Any?) = TextInputDialog()
}