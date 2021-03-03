/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module

data class ColorPickerKey(
    val initialColor: Color,
    val colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
    val title: String? = null,
    val allowCustomArgb: Boolean = true,
    val showAlphaSelector: Boolean = false,
) : Key<Color>

@Module
val colorPickerKeyModule = KeyModule<ColorPickerKey>()

@Given
fun colorPickerUi(
    @Given key: ColorPickerKey,
    @Given navigator: Collector<NavigationAction>,
): KeyUi<ColorPickerKey> = {
    DialogWrapper {
        ColorPickerDialog(
            initialColor = key.initialColor,
            onColorSelected = { navigator(Pop(key, it)) },
            onCancel = { navigator(Pop(key)) },
            colorPalettes = key.colorPalettes,
            showAlphaSelector = key.showAlphaSelector,
            allowCustomArgb = key.allowCustomArgb,
            title = key.title?.let { { Text(it) } }
        )
    }
}

@Given
val colorPickerUiOptionsFactory = DialogKeyUiOptionsFactory<ColorPickerKey>()
