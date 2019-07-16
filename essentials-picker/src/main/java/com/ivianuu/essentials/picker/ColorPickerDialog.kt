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

package com.ivianuu.essentials.picker

import android.view.LayoutInflater
import android.view.ViewGroup
import com.afollestad.materialdialogs.color.colorChooser
import com.ivianuu.essentials.ui.dialog.EsDialogController
import com.ivianuu.essentials.ui.dialog.dialog
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.ResultKey
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.popWithResult
import com.ivianuu.injekt.inject

data class ColorPickerKey(
    val titleRes: Int = R.string.es_dialog_title_color_picker,
    val preselect: Int = 0,
    val allowCustomArgb: Boolean = true,
    val showAlphaSelector: Boolean = false
) : ControllerKey(::ColorPickerDialog, NavOptions().dialog()),
    ResultKey<Int>

/**
 * Color picker controller
 */
class ColorPickerDialog : EsDialogController() {

    private val key by inject<ColorPickerKey>()

    override fun onCreateDialog(inflater: LayoutInflater, container: ViewGroup) = dialog {
        title(key.titleRes)
        colorChooser(
            colors = PRIMARY_COLORS,
            subColors = PRIMARY_COLORS_SUB,
            initialSelection = if (key.preselect != 0) key.preselect else null,
            allowCustomArgb = key.allowCustomArgb,
            showAlphaSelector = key.showAlphaSelector
        ) { _, color -> travelerRouter.popWithResult(key, color) }
        negativeButton(R.string.es_cancel)
    }

}