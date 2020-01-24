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
import androidx.compose.ambientOf
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.graphics.Shape
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Padding
import androidx.ui.layout.Spacer
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.layout.Row

@Immutable
data class FloatingActionButtonStyle(
    val minSize: Dp,
    val shape: Shape = CircleShape,
    val color: Color,
    val elevation: Dp = 6.dp
)

@Composable
fun DefaultFloatingActionButtonStyle(
    shape: Shape = CircleShape,
    color: Color = MaterialTheme.colors().secondary,
    elevation: Dp = 6.dp
) = FloatingActionButtonStyle(
    minSize = 56.dp,
    shape = shape,
    color = color,
    elevation = elevation
)

@Composable
fun MiniFloatingActionButtonStyle(
    shape: Shape = CircleShape,
    color: Color = MaterialTheme.colors().secondary,
    elevation: Dp = 6.dp
) = FloatingActionButtonStyle(
    minSize = 40.dp,
    shape = shape,
    color = color,
    elevation = elevation
)

@Composable
fun ExtendedFloatingActionButtonStyle(
    shape: Shape = CircleShape,
    color: Color = MaterialTheme.colors().secondary,
    elevation: Dp = 6.dp
) = FloatingActionButtonStyle(
    minSize = 48.dp,
    shape = shape,
    color = color,
    elevation = elevation
)

val FloatingActionButtonStyleAmbient =
    ambientOf<FloatingActionButtonStyle?> { null }

@Composable
fun FloatingActionButton(
    modifier: Modifier = Modifier.None,
    onClick: (() -> Unit)? = null,
    style: FloatingActionButtonStyle = FloatingActionButtonStyleAmbient.current
        ?: DefaultFloatingActionButtonStyle(),
    children: @Composable () -> Unit
) {
    androidx.ui.material.FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        minSize = style.minSize,
        shape = style.shape,
        color = style.color,
        elevation = style.elevation,
        children = children
    )
}

@Composable
fun FloatingActionButton(
    icon: Image,
    modifier: Modifier = Modifier.None,
    onClick: (() -> Unit)? = null,
    style: FloatingActionButtonStyle = FloatingActionButtonStyleAmbient.current ?: DefaultFloatingActionButtonStyle()
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        style = style
    ) {
        Icon(image = icon)
    }
}

@Composable
fun FloatingActionButton(
    text: String,
    modifier: Modifier = Modifier.None,
    icon: Image? = null,
    onClick: (() -> Unit)? = null,
    style: FloatingActionButtonStyle = FloatingActionButtonStyleAmbient.current ?: ExtendedFloatingActionButtonStyle()
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        style = style
    ) {
        if (icon == null) {
            Padding(left = 20.dp, right = 20.dp) {
                Text(text = text.toUpperCase())
            }
        } else {
            Padding(left = 12.dp, right = 20.dp) {
                Row(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    Icon(image = icon)
                    Spacer(LayoutWidth(12.dp))
                    Text(text = text.toUpperCase())
                }
            }
        }
    }
}
