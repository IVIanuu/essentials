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
import androidx.ui.core.Modifier
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.Brush
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.ambientOf
import com.ivianuu.essentials.ui.core.current

@Immutable
data class CardStyle(
    val shape: Shape = RectangleShape,
    val color: Color,
    val contentColor: Color,
    val borderWidth: Dp = 0.dp,
    val borderBrush: Brush? = null,
    val elevation: Dp = 1.dp
)

val CardStyleAmbient =
    ambientOf<CardStyle?> { null }

@Composable
fun DefaultCardStyle(
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colors().surface,
    contentColor: Color = guessingContentColorFor(color),
    borderWidth: Dp = 0.dp,
    borderBrush: Brush? = null,
    elevation: Dp = 1.dp
) = CardStyle(
    shape = shape,
    color = color,
    contentColor = contentColor,
    borderBrush = borderBrush,
    borderWidth = borderWidth,
    elevation = elevation
)

@Composable
fun Card(
    modifier: Modifier = Modifier.None,
    style: CardStyle = CardStyleAmbient.current ?: DefaultCardStyle(),
    children: @Composable () -> Unit
) {
    androidx.ui.material.surface.Card(
        modifier = modifier,
        shape = style.shape,
        color = style.color,
        contentColor = style.contentColor,
        borderWidth = style.borderWidth,
        borderBrush = style.borderBrush,
        elevation = style.elevation,
        children = children
    )
}
