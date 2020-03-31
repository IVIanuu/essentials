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
import androidx.ui.foundation.Border
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class CardStyle(
    val shape: Shape = RectangleShape,
    val color: Color,
    val contentColor: Color,
    val border: Border? = null,
    val elevation: Dp = 1.dp,
    val modifier: Modifier
)

val CardStyleAmbient = staticAmbientOf<CardStyle>()

@Composable
fun DefaultCardStyle(
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colors.surface,
    contentColor: Color = guessingContentColorFor(color),
    border: Border? = null,
    elevation: Dp = 1.dp,
    modifier: Modifier = Modifier.None
) = CardStyle(
    shape = shape,
    color = color,
    contentColor = contentColor,
    border = border,
    elevation = elevation,
    modifier = modifier
)

@Composable
fun Card(
    modifier: Modifier = Modifier.None,
    style: CardStyle = CardStyleAmbient.currentOrElse { DefaultCardStyle() },
    content: @Composable () -> Unit
) {
    androidx.ui.material.Card(
        modifier = style.modifier.plus(modifier),
        shape = style.shape,
        color = style.color,
        contentColor = style.contentColor,
        border = style.border,
        elevation = style.elevation,
        content = content
    )
}
