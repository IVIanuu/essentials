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

package com.ivianuu.essentials.ui.prefs

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.graphics.Color
import androidx.ui.layout.preferredSize
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.box.asState
import com.ivianuu.essentials.ui.dialog.ColorPickerDialog
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.material.Surface

@Composable
fun ColorPreference(
    box: Box<Color>,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
    showAlphaSelector: Boolean = true,
    allowCustomArgb: Boolean = true,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
    ColorPreference(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        dialogTitle = dialogTitle,
        colorPalettes = colorPalettes,
        showAlphaSelector = showAlphaSelector,
        allowCustomArgb = allowCustomArgb
    )
}

@Composable
fun ColorPreference(
    value: Color,
    onValueChange: (Color) -> Unit,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
    showAlphaSelector: Boolean = true,
    allowCustomArgb: Boolean = true,
    modifier: Modifier = Modifier
) {
    DialogPreference(
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        trailing = {
            Surface(
                modifier = Modifier.preferredSize(40.dp),
                color = value,
                border = Border(
                    size = 1.dp,
                    color = MaterialTheme.colors.onSurface
                )
            ) {}
        },
        dialog = {
            ColorPickerDialog(
                initialColor = value,
                onColorSelected = onValueChange,
                colorPalettes = colorPalettes,
                showAlphaSelector = showAlphaSelector,
                allowCustomArgb = allowCustomArgb,
                title = dialogTitle
            )
        }
    )
}
