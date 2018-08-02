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

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.compass.Destination
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.essentials.R
import com.ivianuu.essentials.injection.EssentialsFragmentBindingModule
import com.ivianuu.essentials.injection.PerFragment
import com.ivianuu.essentials.ui.base.BaseDialogFragment
import com.ivianuu.essentials.ui.traveler.destination.ResultDestination

@Destination(TextInputDialog::class)
data class TextInputDestination(
    override var resultCode: Int,
    val title: String,
    val inputHint: String = "",
    val inputType: Int = 0,
    val prefill: String = "",
    val allowEmptyInput: Boolean = false
) : ResultDestination

/**
 * Text input dialog
 */
@EssentialsFragmentBindingModule
@PerFragment
@AutoContribute
class TextInputDialog : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val destination = textInputDestination()

        return MaterialDialog.Builder(requireContext())
            .autoDismiss(false)
            .title(destination.title)
            .input(
                destination.inputHint,
                destination.prefill,
                destination.allowEmptyInput
            ) { _, input ->
                router.exitWithResult(destination.resultCode, input.toString())
            }
            .inputType(destination.inputType)
            .positiveText(R.string.action_ok)
            .negativeText(R.string.action_cancel)
            .build()
    }

}