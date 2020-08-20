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

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.ripple.RippleThemeAmbient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    selected: Boolean = false
) {
    val minHeight = if (subtitle != null) {
        if (leading == null) TitleAndSubtitleMinHeight else TitleAndSubtitleMinHeightWithIcon
    } else {
        if (leading == null) TitleOnlyMinHeight else TitleOnlyMinHeightWithIcon
    }

    Box(
        modifier = modifier
            .preferredHeightIn(minHeight = minHeight)
            .fillMaxWidth()
            .background(color = if (selected) RippleThemeAmbient.current.defaultColor() else Color.Transparent)
            .then(
                if (onClick != null || onLongClick != null)
                    Modifier.clickable(
                        enabled = enabled,
                        onClick = onClick ?: {},
                        onLongClick = onLongClick
                    )
                else Modifier
            ),
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
