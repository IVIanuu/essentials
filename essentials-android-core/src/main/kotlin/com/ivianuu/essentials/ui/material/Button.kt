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
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import androidx.ui.layout.InnerPadding
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.DefaultTextComposableStyle
import com.ivianuu.essentials.ui.core.TextComposableStyleAmbient
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class ButtonStyle(
    val backgroundColor: Color,
    val disabledBackgroundColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
    val shape: Shape,
    val border: Border? = null,
    val elevation: Dp = 0.dp,
    val disabledElevation: Dp = 0.dp,
    val padding: InnerPadding = androidx.ui.material.Button.DefaultInnerPadding,
    val modifier: Modifier = Modifier
)

@Composable
fun ContainedButtonStyle(
    backgroundColor: Color = MaterialTheme.colors.primary,
    disabledBackgroundColor: Color = androidx.ui.material.Button.defaultDisabledBackgroundColor,
    contentColor: Color = guessingContentColorFor(backgroundColor),
    disabledContentColor: Color = androidx.ui.material.Button.defaultDisabledContentColor,
    shape: Shape = MaterialTheme.shapes.small,
    border: Border? = null,
    elevation: Dp = 2.dp,
    disabledElevation: Dp = 0.dp,
    padding: InnerPadding = androidx.ui.material.Button.DefaultInnerPadding,
    modifier: Modifier = Modifier
) = ButtonStyle(
    backgroundColor = backgroundColor,
    disabledBackgroundColor = disabledBackgroundColor,
    shape = shape,
    border = border,
    elevation = elevation,
    disabledElevation = disabledElevation,
    contentColor = contentColor,
    disabledContentColor = disabledContentColor,
    padding = padding,
    modifier = modifier
)

@Composable
fun OutlinedButtonStyle(
    border: Border = Border(
        size = 1.dp,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
    ),
    backgroundColor: Color = MaterialTheme.colors.surface,
    disabledBackgroundColor: Color = androidx.ui.material.Button.defaultDisabledBackgroundColor,
    contentColor: Color = guessingContentColorFor(backgroundColor),
    disabledContentColor: Color = androidx.ui.material.Button.defaultDisabledContentColor,
    shape: Shape = MaterialTheme.shapes.small,
    elevation: Dp = 0.dp,
    modifier: Modifier = Modifier
) = ButtonStyle(
    backgroundColor = backgroundColor,
    disabledBackgroundColor = disabledBackgroundColor,
    shape = shape,
    border = border,
    elevation = elevation,
    contentColor = contentColor,
    disabledContentColor = disabledContentColor,
    modifier = modifier
)

@Composable
fun TextButtonStyle(
    elevation: Dp = 0.dp,
    shape: Shape = MaterialTheme.shapes.small,
    border: Border? = null,
    backgroundColor: Color = Color.Transparent,
    disabledBackgroundColor: Color = androidx.ui.material.Button.defaultDisabledBackgroundColor,
    contentColor: Color = MaterialTheme.colors.primary,
    disabledContentColor: Color = androidx.ui.material.Button.defaultDisabledContentColor,
    padding: InnerPadding = androidx.ui.material.TextButton.DefaultInnerPadding,
    modifier: Modifier = Modifier
) = ButtonStyle(
    elevation = elevation,
    backgroundColor = backgroundColor,
    disabledBackgroundColor = disabledBackgroundColor,
    shape = shape,
    border = border,
    contentColor = contentColor,
    disabledContentColor = disabledContentColor,
    padding = padding,
    modifier = modifier
)

val ButtonStyleAmbient = staticAmbientOf<ButtonStyle>()

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ButtonStyle = ButtonStyleAmbient.currentOrElse { ContainedButtonStyle() },
    text: @Composable () -> Unit
) {
    androidx.ui.material.Button(
        modifier = style.modifier + modifier,
        onClick = onClick,
        enabled = enabled,
        backgroundColor = style.backgroundColor,
        contentColor = style.contentColor,
        shape = style.shape,
        border = style.border,
        elevation = style.elevation,
        padding = style.padding
    ) {
        Providers(
            TextComposableStyleAmbient provides DefaultTextComposableStyle(
                uppercase = true,
                maxLines = 1
            )
        ) {
            ProvideTextStyle(
                MaterialTheme.typography.button,
                children = text
            )
        }
    }
}
