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
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.LongPressGestureDetector
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.DrawBackground
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.ripple.RippleThemeAmbient
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.currentOrNull
import com.ivianuu.essentials.ui.layout.AddPaddingIfNeededLayout
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.Row

@Immutable
data class ListItemStyle(
    val contentPadding: EdgeInsets = ContentPadding
)

@Composable
fun DefaultListItemStyle(
    contentPadding: EdgeInsets = ContentPadding
) = ListItemStyle(
    contentPadding = contentPadding
)

val ListItemStyleAmbient = staticAmbientOf<ListItemStyle>()

@Composable
fun ListItem(
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    style: ListItemStyle = ListItemStyleAmbient.currentOrNull ?: DefaultListItemStyle()
) {
    val minHeight = if (subtitle != null) {
        if (leading == null) TitleAndSubtitleMinHeight else TitleAndSubtitleMinHeightWithIcon
    } else {
        if (leading == null) TitleOnlyMinHeight else TitleOnlyMinHeightWithIcon
    }

    val content: @Composable () -> Unit = {
        Container(
            modifier = DrawBackground(
                color = if (selected) contentColor().copy(
                    alpha = RippleThemeAmbient.current.opacity()
                ) else Color.Transparent
            ) + (onLongClick?.let {
                LongPressGestureDetector {
                    if (enabled) onLongClick()
                }
            } ?: Modifier.None),
            constraints = DpConstraints(minHeight = minHeight),
            padding = EdgeInsets(
                left = style.contentPadding.left,
                right = style.contentPadding.right
            )
        ) {
            Row(crossAxisAlignment = CrossAxisAlignment.Center) {
                // leading
                if (leading != null) {
                    Container(
                        modifier = LayoutInflexible,
                        alignment = Alignment.CenterStart
                    ) {
                        AddPaddingIfNeededLayout(
                            padding = EdgeInsets(
                                top = style.contentPadding.top,
                                bottom = style.contentPadding.bottom
                            )
                        ) {
                            ProvideEmphasis(
                                emphasis = EmphasisAmbient.current.high,
                                children = leading
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
                                CurrentTextStyleProvider(value = MaterialTheme.typography().subtitle1) {
                                    ProvideEmphasis(emphasis = EmphasisAmbient.current.high, children = title)
                                }
                            }
                            if (subtitle != null) {
                                CurrentTextStyleProvider(value = MaterialTheme.typography().body2) {
                                    ProvideEmphasis(emphasis = EmphasisAmbient.current.medium, children = subtitle)
                                }
                            }
                        }
                    }
                }

                // trailing
                if (trailing != null) {
                    Container(
                        modifier = LayoutInflexible,
                        constraints = DpConstraints(minHeight = minHeight)
                    ) {
                        AddPaddingIfNeededLayout(
                            padding = EdgeInsets(
                                top = style.contentPadding.top,
                                bottom = style.contentPadding.bottom
                            )
                        ) {
                            ProvideEmphasis(
                                emphasis = EmphasisAmbient.current.high,
                                children = trailing
                            )
                        }
                    }
                }
            }
        }
    }

    Ripple(
        bounded = true,
        enabled = enabled && (onClick != null || onLongClick != null)
    ) {
        Clickable(
            onClick = onClick ?: { },
            enabled = enabled && onClick != null,
            children = content
        )
    }
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
