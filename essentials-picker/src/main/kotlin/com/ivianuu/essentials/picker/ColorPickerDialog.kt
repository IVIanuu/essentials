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

import com.afollestad.materialdialogs.color.colorChooser
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.NavigatorAmbient
import com.ivianuu.compose.state
import com.ivianuu.essentials.ui.dialog.DialogRoute

fun ComponentComposition.ColorPickerRoute(
    title: String? = null,
    titleRes: Int? = R.string.es_dialog_title_color_picker,
    preselect: Int,
    allowCustomArgb: Boolean = true,
    showAlphaSelector: Boolean = false
) = DialogRoute {
    val (currentColor, setCurrentColor) = state { preselect }
    val navigator = ambient(NavigatorAmbient)
    buildDialog {
        title(res = titleRes, text = title)
        colorChooser(
            colors = PRIMARY_COLORS,
            subColors = PRIMARY_COLORS_SUB,
            initialSelection = currentColor,
            allowCustomArgb = allowCustomArgb,
            showAlphaSelector = showAlphaSelector,
            waitForPositiveButton = false
        ) { _, color -> setCurrentColor(color) }
        positiveButton(R.string.es_ok) { navigator.pop(currentColor) }
        negativeButton(R.string.es_cancel)
    }
}