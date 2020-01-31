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
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import androidx.ui.graphics.Brush
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import androidx.ui.graphics.SolidColor
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.MaterialTheme
import androidx.ui.material.contentColorFor
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class ButtonStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val shape: Shape,
    val borderWidth: Dp = 0.dp,
    val borderBrush: Brush? = null,
    val elevation: Dp = 0.dp,
    val paddings: EdgeInsets = EdgeInsets(
        left = 16.dp,
        top = 8.dp,
        right = 16.dp,
        bottom = 8.dp
    )
)

@Composable
fun ContainedButtonStyle(
    backgroundColor: Color = MaterialTheme.colors().primary,
    contentColor: Color = contentColorFor(backgroundColor),
    shape: Shape = MaterialTheme.shapes().button,
    elevation: Dp = 2.dp
) = ButtonStyle(
    backgroundColor = backgroundColor,
    shape = shape,
    elevation = elevation,
    contentColor = contentColor
)

@Composable
fun OutlinedButtonStyle(
    borderWidth: Dp = 1.dp,
    borderBrush: Brush = SolidColor(
        MaterialTheme.colors().onSurface.copy(alpha = 0.12f)
    ),
    backgroundColor: Color = MaterialTheme.colors().surface,
    contentColor: Color = MaterialTheme.colors().primary,
    shape: Shape = MaterialTheme.shapes().button,
    elevation: Dp = 0.dp
) = ButtonStyle(
    backgroundColor = backgroundColor,
    shape = shape,
    borderBrush = borderBrush,
    borderWidth = borderWidth,
    elevation = elevation,
    contentColor = contentColor
)

@Composable
fun TextButtonStyle(
    shape: Shape = MaterialTheme.shapes().button,
    contentColor: Color = MaterialTheme.colors().primary
) = ButtonStyle(
    backgroundColor = Color.Transparent,
    shape = shape,
    paddings = EdgeInsets(all = 8.dp),
    contentColor = contentColor
)

val ButtonStyleAmbient = staticAmbientOf<ButtonStyle>()

@Composable
fun Button(
    modifier: Modifier = Modifier.None,
    onClick: (() -> Unit)? = null,
    style: ButtonStyle = ButtonStyleAmbient.currentOrElse { ContainedButtonStyle() },
    children: @Composable () -> Unit
) {
    androidx.ui.material.Button(
        modifier = modifier,
        onClick = onClick,
        style = style.toAndroidxButtonStyle(),
        children = children
    )
}

@Composable
fun Button(
    text: String,
    modifier: Modifier = Modifier.None,
    onClick: (() -> Unit)? = null,
    style: ButtonStyle = ButtonStyleAmbient.currentOrElse { ContainedButtonStyle() }
) {
    androidx.ui.material.Button(
        modifier = modifier,
        onClick = onClick,
        style = style.toAndroidxButtonStyle(),
        text = text.toUpperCase()
    )
}

@Composable
private fun ButtonStyle.toAndroidxButtonStyle() = remember(this) {
    androidx.ui.material.ButtonStyle(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        shape = shape,
        borderWidth = borderWidth,
        borderBrush = borderBrush,
        elevation = elevation,
        paddings = paddings
    )
}
