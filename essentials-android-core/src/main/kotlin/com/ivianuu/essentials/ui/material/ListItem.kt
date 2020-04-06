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
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.LongPressGestureDetector
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeightIn
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.currentOrElse
import com.ivianuu.essentials.ui.layout.AddPaddingIfNeededLayout
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.Row

@Immutable
data class ListItemStyle(
    val contentPadding: EdgeInsets,
    val modifier: Modifier
)

@Composable
fun DefaultListItemStyle(
    contentPadding: EdgeInsets = ContentPadding,
    modifier: Modifier = Modifier.None
) = ListItemStyle(
    contentPadding = contentPadding,
    modifier = modifier
)

val ListItemStyleAmbient = staticAmbientOf<ListItemStyle>()

@Composable
fun ListItem(
    modifier: Modifier = Modifier.None,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    style: ListItemStyle = ListItemStyleAmbient.currentOrElse { DefaultListItemStyle() }
) {
    val minHeight = if (subtitle != null) {
        if (leading == null) TitleAndSubtitleMinHeight else TitleAndSubtitleMinHeightWithIcon
    } else {
        if (leading == null) TitleOnlyMinHeight else TitleOnlyMinHeightWithIcon
    }

    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier.preferredHeightIn(minHeight = minHeight)
                .padding(start = style.contentPadding.left, end = style.contentPadding.right)
                .drawBackground(color = if (selected) defaultRippleColor() else Color.Transparent)
                .plus(onLongClick?.let {
                    LongPressGestureDetector {
                        if (enabled) onLongClick()
                    }
                } ?: Modifier.None)
                .plus(style.modifier)
                .plus(modifier),
            crossAxisAlignment = CrossAxisAlignment.Center
        ) {
            // leading
            if (leading != null) {
                Container(alignment = Alignment.CenterStart) {
                    AddPaddingIfNeededLayout(
                        padding = EdgeInsets(
                            top = style.contentPadding.top,
                            bottom = style.contentPadding.bottom
                        )
                    ) {
                        ProvideEmphasis(
                            emphasis = EmphasisAmbient.current.high,
                            content = leading
                        )
                    }
                }
            }

            // content
            Container(
                modifier = LayoutFlexible(1f),
                padding = EdgeInsets(
                    left = if (leading != null) HorizontalTextPadding else 0.dp,
                    right = if (trailing != null) HorizontalTextPadding else 0.dp
                ),
                alignment = Alignment.CenterStart
            ) {
                AddPaddingIfNeededLayout(
                    padding = EdgeInsets(
                        top = style.contentPadding.top,
                        bottom = style.contentPadding.bottom
                    )
                ) {
                    Column {
                        if (title != null) {
                            ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
                                ProvideEmphasis(
                                    emphasis = EmphasisAmbient.current.high,
                                    content = title
                                )
                            }
                        }
                        if (subtitle != null) {
                            ProvideTextStyle(value = MaterialTheme.typography.body2) {
                                ProvideEmphasis(
                                    emphasis = EmphasisAmbient.current.medium,
                                    content = subtitle
                                )
                            }
                        }
                    }
                }
            }

            // trailing
            if (trailing != null) {
                Container(constraints = DpConstraints(minHeight = minHeight)) {
                    AddPaddingIfNeededLayout(
                        padding = EdgeInsets(
                            top = style.contentPadding.top,
                            bottom = style.contentPadding.bottom
                        )
                    ) {
                        ProvideEmphasis(
                            emphasis = EmphasisAmbient.current.high,
                            content = trailing
                        )
                    }
                }
            }
        }
    }

    Clickable(
        onClick = onClick ?: { },
        enabled = enabled && onClick != null,
        modifier = if (onClick != null || onLongClick != null) Modifier.ripple(enabled = enabled) else Modifier.None,
        children = content
    )
}

private val TitleOnlyMinHeight = 48.dp
private val TitleOnlyMinHeightWithIcon = 56.dp
private val TitleAndSubtitleMinHeight = 64.dp
private val TitleAndSubtitleMinHeightWithIcon = 72.dp
private val HorizontalTextPadding = 16.dp
private val ContentPadding = EdgeInsets(
    left = 16.dp,
    top = 8.dp,
    right = 16.dp,
    bottom = 8.dp
)
