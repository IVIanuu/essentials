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
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.dp
import androidx.ui.core.gesture.LongPressGestureDetector
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.graphics.toArgb
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Typography
import androidx.ui.material.ripple.Ripple
import androidx.ui.text.TextStyle
import com.ivianuu.essentials.ui.compose.common.asIconComposable
import com.ivianuu.essentials.ui.compose.common.asTextComposable
import com.ivianuu.essentials.ui.compose.core.CurrentBackground
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.core.invokeAsComposable
import com.ivianuu.essentials.ui.compose.layout.Column
import com.ivianuu.essentials.ui.compose.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.Row
import com.ivianuu.essentials.util.isDark

@Composable
fun SimpleListItem(
    title: String? = null,
    subtitle: String? = null,
    image: Image? = null,
    enabled: Boolean = true,
    contentPadding: EdgeInsets = ContentPadding,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) = composable {
    SimpleListItem(
        title = title.asTextComposable(),
        subtitle = subtitle.asTextComposable(),
        leading = image.asIconComposable(),
        enabled = enabled,
        contentPadding = contentPadding,
        onClick = onClick,
        onLongClick = onLongClick
    )
}

@Composable
fun SimpleListItem(
    title: (@Composable() () -> Unit)? = null,
    subtitle: (@Composable() () -> Unit)? = null,
    leading: (@Composable() () -> Unit)? = null,
    trailing: (@Composable() () -> Unit)? = null,
    enabled: Boolean = true,
    contentPadding: EdgeInsets = ContentPadding,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) = composable {
    val styledTitle = applyTextStyle(
        TitleTextStyle,
        title
    )
    val styledSubtitle = applyTextStyle(
        SubtitleTextStyle,
        subtitle
    )
    val styledLeading = applyIconStyle(leading)
    val styledTrailing = applyIconStyle(trailing)

    val item = @Composable {
        val minHeight = if (subtitle != null) {
            if (leading == null) TitleAndSubtitleMinHeight else TitleAndSubtitleMinHeightWithIcon
        } else {
            if (leading == null) TitleOnlyMinHeight else TitleOnlyMinHeightWithIcon
        }

        Container(
            constraints = DpConstraints(minHeight = minHeight),
            padding = contentPadding
        ) {
            Row(crossAxisAlignment = CrossAxisAlignment.Center) {
                // leading
                if (styledLeading != null) {
                    Container(
                        modifier = Inflexible,
                        alignment = Alignment.CenterLeft,
                        children = styledLeading
                    )
                }

                // content
                Container(
                    modifier = Flexible(1f),
                    padding = EdgeInsets(
                        left = if (styledLeading != null) HorizontalTextPadding else 0.dp,
                        right = if (styledTrailing != null) HorizontalTextPadding else 0.dp
                    ),
                    alignment = Alignment.CenterLeft
                ) {
                    Column(
                        mainAxisAlignment = MainAxisAlignment.Center
                    ) {
                        styledTitle?.invokeAsComposable()
                        styledSubtitle?.invokeAsComposable()
                    }
                }

                // trailing
                if (styledTrailing != null) {
                    Container(
                        modifier = Inflexible,
                        constraints = DpConstraints(minHeight = minHeight),
                        children = styledTrailing
                    )
                }
            }
        }
    }

    Ripple(
        bounded = true,
        enabled = onClick != null || onLongClick != null
    ) {
        LongPressGestureDetector(
            onLongPress = { if (enabled) onLongClick?.invoke() }
        ) {
            Clickable(
                onClick = if (enabled) onClick else null,
                children = item
            )
        }
    }
}

private val TitleOnlyMinHeight = 48.dp
private val TitleOnlyMinHeightWithIcon = 56.dp
private val TitleAndSubtitleMinHeight = 64.dp
private val TitleAndSubtitleMinHeightWithIcon = 72.dp
private val HorizontalTextPadding = 16.dp
private val ContentPadding = EdgeInsets(
    left = 16.dp,
    right = 16.dp
)

private data class ListItemTextStyle(
    val style: Typography.() -> TextStyle,
    val color: ColorPalette.() -> Color,
    val opacity: Float
)

private fun applyTextStyle(
    textStyle: ListItemTextStyle,
    children: (@Composable() () -> Unit)?
): (@Composable() () -> Unit)? {
    if (children == null) return null
    return {
        val colors = MaterialTheme.colors()()
        val typography = MaterialTheme.typography()()
        val textColor = textStyle.color(colors).copy(alpha = textStyle.opacity)
        val appliedTextStyle = textStyle.style(typography).copy(color = textColor)
        CurrentTextStyleProvider(appliedTextStyle, children)
    }
}

private fun applyIconStyle(
    children: (@Composable() () -> Unit)?
): (@Composable() () -> Unit)? {
    if (children == null) return null
    return {
        val iconAlpha =
            if (ambient(CurrentBackground).toArgb().isDark) IconOpacityDark else IconOpacity
        val iconColor = currentIconStyle().color ?: colorForCurrentBackground()
            .copy(alpha = iconAlpha)
        val appliedIconStyle = currentIconStyle().copy(color = iconColor)
        CurrentIconStyleProvider(appliedIconStyle, children)
    }
}

private const val PrimaryTextOpacity = 0.87f
private const val SecondaryTextOpacity = 0.6f
private const val IconOpacity = 0.87f
private const val IconOpacityDark = 0.87f

private val TitleTextStyle =
    ListItemTextStyle(
        { subtitle1 },
        { onSurface },
        PrimaryTextOpacity
    )
private val SubtitleTextStyle =
    ListItemTextStyle(
        { body2 },
        { onSurface },
        SecondaryTextOpacity
    )
