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

package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.engine.geometry.Shape
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.foundation.shape.border.Border
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.invoke

@Composable
fun EsSurface(
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colors()().surface,
    border: Border? = null,
    elevation: Dp = 0.dp,
    children: @Composable() () -> Unit
) = composable {
    Surface(
        shape = shape,
        color = color,
        border = border,
        elevation = elevation
    ) {
        CurrentIconStyleProvider(
            value = IconStyle(color = colorForCurrentBackground()),
            children = children
        )
    }
}