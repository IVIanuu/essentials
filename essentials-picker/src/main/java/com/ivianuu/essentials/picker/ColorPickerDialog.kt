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
import com.afollestad.materialdialogs.color.colorChooser
import com.ivianuu.essentials.ui.base.EsDialogController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.bindKey
import com.ivianuu.traveler.result.goBackWithResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ColorPickerKey(
    val titleRes: Int = R.string.es_dialog_title_color_picker,
    val preselect: Int = 0,
    val allowCustomArgb: Boolean = true,
    val showAlphaSelector: Boolean = false,
    val resultCode: Int
) : ControllerKey(ColorPickerDialog::class, NavOptions().dialog())

/**
 * Color picker controller
 */
class ColorPickerDialog : EsDialogController() {

    private val key by bindKey<ColorPickerKey>()

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        return MaterialDialog(activity)
            .title(key.titleRes)
            .colorChooser(
                colors = PRIMARY_COLORS,
                subColors = PRIMARY_COLORS_SUB,
                initialSelection = if (key.preselect != 0) key.preselect else null,
                allowCustomArgb = key.allowCustomArgb,
                showAlphaSelector = key.showAlphaSelector
            ) { _, color -> travelerRouter.goBackWithResult(key.resultCode, color) }
            .negativeButton(R.string.es_action_cancel)
    }

}