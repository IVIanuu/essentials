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
import androidx.compose.Providers
import androidx.compose.staticAmbientOf
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeightIn
import androidx.ui.layout.preferredWidth
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.DefaultTextComposableStyle
import com.ivianuu.essentials.ui.core.TextComposableStyleAmbient
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class FloatingActionButtonStyle(
    val shape: Shape,
    val backgroundColor: Color,
    val contentColor: Color,
    val elevation: Dp,
    val modifier: Modifier = Modifier
)

@Composable
fun DefaultFloatingActionButtonStyle(
    shape: Shape = CircleShape,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    contentColor: Color = guessingContentColorFor(backgroundColor),
    elevation: Dp = 6.dp,
    modifier: Modifier = Modifier
) = FloatingActionButtonStyle(
    shape = shape,
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    elevation = elevation,
    modifier = Modifier.preferredHeightIn(minHeight = 56.dp).plus(modifier)
)

@Composable
fun MiniFloatingActionButtonStyle(
    shape: Shape = CircleShape,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    contentColor: Color = guessingContentColorFor(backgroundColor),
    elevation: Dp = 6.dp,
    modifier: Modifier = Modifier
) = FloatingActionButtonStyle(
    shape = shape,
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    elevation = elevation,
    modifier = Modifier.preferredHeightIn(minHeight = 40.dp).plus(modifier)
)

@Composable
fun ExtendedFloatingActionButtonStyle(
    shape: Shape = CircleShape,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    contentColor: Color = guessingContentColorFor(backgroundColor),
    elevation: Dp = 6.dp,
    modifier: Modifier = Modifier
) = FloatingActionButtonStyle(
    shape = shape,
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    elevation = elevation,
    modifier = Modifier.preferredHeightIn(minHeight = 48.dp).plus(modifier)
)

val FloatingActionButtonStyleAmbient = staticAmbientOf<FloatingActionButtonStyle>()

@Composable
fun FloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    style: FloatingActionButtonStyle = FloatingActionButtonStyleAmbient.currentOrElse { DefaultFloatingActionButtonStyle() },
    children: @Composable () -> Unit
) {
    Surface(
        shape = style.shape,
        color = style.backgroundColor,
        contentColor = style.contentColor,
        elevation = style.elevation
    ) {
        ProvideTextStyle(MaterialTheme.typography.button) {
            Box(
                modifier = style.modifier
                    .clickable(onClick = onClick)
                    .plus(modifier),
                gravity = ContentGravity.Center
            ) {
                Providers(
                    TextComposableStyleAmbient provides DefaultTextComposableStyle(
                        uppercase = true,
                        maxLines = 1
                    )
                ) {
                    ProvideTextStyle(
                        MaterialTheme.typography.button,
                        children = children
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingActionButton(
    text: @Composable () -> Unit,
    icon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    style: FloatingActionButtonStyle = FloatingActionButtonStyleAmbient.currentOrElse { ExtendedFloatingActionButtonStyle() }
) {
    FloatingActionButton(
        modifier = style.modifier + modifier,
        onClick = onClick,
        style = style
    ) {
        if (icon != null) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalGravity = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 12.dp, end = 20.dp)
            ) {
                icon()
                Spacer(Modifier.preferredWidth(12.dp))
                text()
            }
        }
        else {
            Box(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                children = text
            )
        }
    }
}
