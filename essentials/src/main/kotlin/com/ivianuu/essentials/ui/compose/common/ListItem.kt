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

package com.ivianuu.essentials.ui.compose.common

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Row
import androidx.ui.material.MaterialColors
import androidx.ui.material.MaterialTypography
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.themeColor
import androidx.ui.material.themeTextStyle
import androidx.ui.text.TextStyle
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun SimpleListItem(
    title: @Composable() (() -> Unit),
    subtitle: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    trailing: @Composable() (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) = composable("SimpleListItem") {
    val styledTitle = applyTextStyle(TitleTextStyle, title)!!
    val styledSubtitle = applyTextStyle(SubtitleTextStyle, subtitle)

    val item = @Composable {
        val minHeight = if (subtitle != null) {
            if (leading == null) TitleAndSubtitleMinHeight else TitleAndSubtitleMinHeightWithIcon
        } else {
            if (leading == null) TitleOnlyMinHeight else TitleOnlyMinHeightWithIcon
        }

        ConstrainedBox(constraints = DpConstraints(minHeight = minHeight)) {
            Row(crossAxisAlignment = CrossAxisAlignment.Center) {
                // leading
                if (leading != null) {
                    Container(
                        modifier = Inflexible,
                        alignment = Alignment.CenterLeft,
                        constraints = DpConstraints(
                            minWidth = IconLeftPadding + IconMinPaddedWidth
                        ),
                        padding = EdgeInsets(
                            left = IconLeftPadding,
                            top = IconVerticalPadding,
                            bottom = IconVerticalPadding
                        ),
                        children = leading
                    )
                }

                // content
                Container(
                    modifier = Flexible(1f),
                    alignment = Alignment.CenterLeft,
                    padding = EdgeInsets(
                        left = ContentLeftPadding,
                        top = ContentVerticalPadding,
                        right = ContentRightPadding,
                        bottom = ContentVerticalPadding
                    )
                ) {
                    Column(
                        mainAxisAlignment = MainAxisAlignment.Center
                    ) {
                        styledTitle()
                        styledSubtitle?.invoke()
                    }
                }

                // trailing
                if (trailing != null) {
                    Container(
                        modifier = Inflexible,
                        padding = EdgeInsets(right = TrailingRightPadding),
                        constraints = DpConstraints(minHeight = minHeight),
                        children = trailing
                    )
                }
            }
        }
    }

    if (onClick != null) {
        val rippleColor = (+themeColor { onSurface }).copy(alpha = RippleOpacity)
        Ripple(bounded = true, color = rippleColor) {
            Clickable(onClick = onClick, children = item)
        }
    } else {
        item()
    }
}

private val TitleOnlyMinHeight = 48.dp
private val TitleOnlyMinHeightWithIcon = 56.dp
private val TitleAndSubtitleMinHeight = 64.dp
private val TitleAndSubtitleMinHeightWithIcon = 72.dp

private val IconMinPaddedWidth = 40.dp
private val IconLeftPadding = 16.dp
private val IconVerticalPadding = 8.dp
private val ContentLeftPadding = 16.dp
private val ContentRightPadding = 16.dp
private val ContentVerticalPadding = 8.dp
private val TrailingRightPadding = 16.dp

private data class ListItemTextStyle(
    val style: MaterialTypography.() -> TextStyle,
    val color: MaterialColors.() -> Color,
    val opacity: Float
)

private fun applyTextStyle(
    textStyle: ListItemTextStyle,
    children: @Composable() (() -> Unit)?
): @Composable() (() -> Unit)? {
    if (children == null) return null
    return {
        val textColor = (+themeColor(textStyle.color)).copy(alpha = textStyle.opacity)
        val appliedTextStyle = (+themeTextStyle(textStyle.style)).copy(color = textColor)
        CurrentTextStyleProvider(appliedTextStyle, children)
    }
}

private const val PrimaryTextOpacity = 0.87f
private const val SecondaryTextOpacity = 0.6f
private const val RippleOpacity = 0.16f
private val TitleTextStyle = ListItemTextStyle({ subtitle1 }, { onSurface }, PrimaryTextOpacity)
private val SubtitleTextStyle = ListItemTextStyle({ body2 }, { onSurface }, SecondaryTextOpacity)
