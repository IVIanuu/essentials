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
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import com.ivianuu.essentials.ui.compose.common.asIconComposable
import com.ivianuu.essentials.ui.compose.common.asTextComposable
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.layout.Column
import com.ivianuu.essentials.ui.compose.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.Row

@Composable
fun SimpleListItem(
    title: String? = null,
    subtitle: String? = null,
    image: Image? = null,
    enabled: Boolean = true,
    contentPadding: EdgeInsets = ContentPadding,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
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
) {
    val styledTitle: (@Composable() () -> Unit)? = if (title == null) null else ({
        CurrentTextStyleProvider(value = MaterialTheme.typography()().subtitle1) {
            EmphasisProvider(emphasis = ambient(EmphasisAmbient).high, children = title)
        }
    })
    val styledSubtitle: (@Composable() () -> Unit)? = if (subtitle == null) null else ({
        CurrentTextStyleProvider(value = MaterialTheme.typography()().body2) {
            EmphasisProvider(emphasis = ambient(EmphasisAmbient).medium, children = subtitle)
        }
    })
    val styledLeading: (@Composable() () -> Unit)? = if (leading == null) null else ({
        EmphasisProvider(emphasis = ambient(EmphasisAmbient).high, children = leading)
    })
    val styledTrailing: (@Composable() () -> Unit)? = if (trailing == null) null else ({
        EmphasisProvider(emphasis = ambient(EmphasisAmbient).high, children = trailing)
    })

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
                        styledTitle?.invoke()
                        styledSubtitle?.invoke()
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