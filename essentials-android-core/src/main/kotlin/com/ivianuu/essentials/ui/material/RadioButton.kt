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

package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.staticAmbientOf
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class RadioButtonStyle(val color: Color)

val RadioButtonStyleAmbient = staticAmbientOf<RadioButtonStyle>()

@Composable
fun DefaultRadioButtonStyle(color: Color = MaterialTheme.colors().secondary) =
    RadioButtonStyle(color = color)

@Composable
fun RadioButton(
    selected: Boolean,
    onSelect: (() -> Unit)?,
    style: RadioButtonStyle = RadioButtonStyleAmbient.currentOrElse { DefaultRadioButtonStyle() },
) {
    androidx.ui.material.RadioButton(
        selected = selected,
        onSelect = onSelect,
        color = style.color
    )
}
