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
import androidx.ui.core.gesture.LongPressDragObserver
import androidx.ui.core.gesture.longPressDragGestureFilter
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.InnerPadding
import androidx.ui.layout.heightIn
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeightIn
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.unit.PxPosition
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.currentOrElse
import com.ivianuu.essentials.ui.layout.AddPaddingIfNeededLayout
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.Row

@Immutable
data class ListItemStyle(
    val contentPadding: InnerPadding,
    val modifier: Modifier
)

@Composable
fun DefaultListItemStyle(
    contentPadding: InnerPadding = ContentPadding,
    modifier: Modifier = Modifier
) = ListItemStyle(
    contentPadding = contentPadding,
    modifier = modifier
)

val ListItemStyleAmbient = staticAmbientOf<ListItemStyle>()

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
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
                .padding(start = style.contentPadding.start, end = style.contentPadding.end)
                .drawBackground(color = if (selected) defaultRippleColor() else Color.Transparent)
                .plus(onLongClick?.let {
                    Modifier.longPressDragGestureFilter(
                        object : LongPressDragObserver {
                            override fun onLongPress(pxPosition: PxPosition) {
                                super.onLongPress(pxPosition)
                                if (enabled) onLongClick()
                            }
                        }
                    )
                } ?: Modifier)
                .plus(style.modifier)
                .plus(modifier),
            crossAxisAlignment = CrossAxisAlignment.Center
        ) {
            // leading
            if (leading != null) {
                Box(gravity = ContentGravity.CenterStart) {
                    AddPaddingIfNeededLayout(
                        padding = InnerPadding(
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
            Box(
                modifier = LayoutFlexible(1f),
                paddingStart = if (leading != null) HorizontalTextPadding else 0.dp,
                paddingEnd = if (trailing != null) HorizontalTextPadding else 0.dp,
                gravity = ContentGravity.CenterStart
            ) {
                AddPaddingIfNeededLayout(
                    padding = InnerPadding(
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
                Box(
                    modifier = Modifier.heightIn(minHeight = minHeight),
                    gravity = Alignment.CenterEnd
                ) {
                    AddPaddingIfNeededLayout(
                        padding = InnerPadding(
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
        modifier = if (onClick != null || onLongClick != null) Modifier.ripple(enabled = enabled) else Modifier,
        children = content
    )
}

private val TitleOnlyMinHeight = 48.dp
private val TitleOnlyMinHeightWithIcon = 56.dp
private val TitleAndSubtitleMinHeight = 64.dp
private val TitleAndSubtitleMinHeightWithIcon = 72.dp
private val HorizontalTextPadding = 16.dp
private val ContentPadding = InnerPadding(
    start = 16.dp,
    top = 8.dp,
    end = 16.dp,
    bottom = 8.dp
)
