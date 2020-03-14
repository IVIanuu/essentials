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
import androidx.ui.core.Modifier
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Slider
import androidx.ui.material.SliderPosition
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class SliderStyle(val color: Color)

val SliderStyleAmbient = staticAmbientOf<SliderStyle>()

@Composable
fun DefaultSliderStyle(color: Color = MaterialTheme.colors().secondary) =
    SliderStyle(color = color)

@Composable
fun Slider(
    position: SliderPosition,
    onValueChange: (Float) -> Unit = { position.value = it },
    modifier: Modifier = Modifier.None,
    onValueChangeEnd: () -> Unit = {},
    style: SliderStyle = SliderStyleAmbient.currentOrElse { DefaultSliderStyle() }
) {
    Slider(
        position = position,
        onValueChange = onValueChange,
        modifier = modifier,
        onValueChangeEnd = onValueChangeEnd,
        color = style.color
    )
}
