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
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.foundation.clickable
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.InnerPadding
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredHeightIn
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.ripple.RippleThemeAmbient
import androidx.ui.unit.dp

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
    onLongClick: (() -> Unit)? = null
) {
    val minHeight = if (subtitle != null) {
        if (leading == null) TitleAndSubtitleMinHeight else TitleAndSubtitleMinHeightWithIcon
    } else {
        if (leading == null) TitleOnlyMinHeight else TitleOnlyMinHeightWithIcon
    }

    Box(
        modifier = Modifier
            .preferredHeightIn(minHeight = minHeight)
            .fillMaxWidth()
            .drawBackground(color = if (selected) RippleThemeAmbient.current.defaultColor() else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick ?: {}, onLongClick = onLongClick)
            .plus(modifier),
        gravity = Alignment.CenterStart
    ) {
        Row(verticalGravity = Alignment.CenterVertically) {
            // leading
            if (leading != null) {
                Box(
                    modifier = Modifier
                        .preferredHeight(minHeight),
                    gravity = ContentGravity.CenterStart
                ) {
                    Box(
                        paddingStart = 16.dp,
                        paddingTop = 8.dp,
                        paddingBottom = 8.dp,
                        gravity = ContentGravity.Center
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
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = HorizontalTextPadding),
                gravity = ContentGravity.CenterStart
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            bottom = 8.dp
                        ),
                    verticalArrangement = Arrangement.Center
                ) {
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

            // trailing
            if (trailing != null) {
                Box(
                    modifier = Modifier
                        .preferredHeight(minHeight),
                    gravity = ContentGravity.CenterEnd
                ) {
                    Box(
                        paddingTop = 8.dp,
                        paddingEnd = 16.dp,
                        paddingBottom = 8.dp,
                        gravity = ContentGravity.Center
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
