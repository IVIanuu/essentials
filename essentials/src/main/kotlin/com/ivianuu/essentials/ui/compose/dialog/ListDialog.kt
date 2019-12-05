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

package com.ivianuu.essentials.ui.compose.dialog

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.ExpandedWidth
import androidx.ui.material.ripple.Ripple
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollPosition
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollableList
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.invokeAsComposable
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.Row
import com.ivianuu.essentials.ui.compose.layout.WidthSpacer

@Composable
fun ListDialog(
    listCount: Int,
    itemSizeProvider: (Int) -> Dp,
    scrollPosition: ScrollPosition = remember { ScrollPosition() },
    scrollDirection: Axis = Axis.Vertical,
    scrollingEnabled: Boolean = true,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: (@Composable() () -> Unit)? = null,
    title: (@Composable() () -> Unit)? = null,
    positiveButton: (@Composable() () -> Unit)? = null,
    negativeButton: (@Composable() () -> Unit)? = null,
    neutralButton: (@Composable() () -> Unit)? = null,
    listItem: @Composable() (Int) -> Unit
) = composable {
    MaterialDialog(
        icon = icon,
        title = title,
        showTopDivider = scrollPosition.value > scrollPosition.minValue,
        showBottomDivider = scrollPosition.value < scrollPosition.maxValue,
        applyContentPadding = false,
        buttonLayout = buttonLayout,
        content = {
            ScrollableList(
                count = listCount,
                itemSizeProvider = itemSizeProvider,
                position = scrollPosition,
                direction = scrollDirection,
                enabled = scrollingEnabled,
                item = listItem
            )
        },
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        neutralButton = neutralButton
    )
}

@Composable
fun SimpleDialogListItem(
    leading: (@Composable() () -> Unit)? = null,
    title: @Composable() () -> Unit,
    onClick: (() -> Unit)? = null
) = composable {
    Ripple(bounded = true) {
        Clickable(onClick = onClick) {
            Container(
                modifier = ExpandedWidth,
                height = 48.dp,
                padding = EdgeInsets(
                    left = 24.dp,
                    right = 24.dp
                ),
                alignment = Alignment.CenterLeft
            ) {
                Row(
                    mainAxisAlignment = MainAxisAlignment.End,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    if (leading != null) {
                        leading.invokeAsComposable()
                        WidthSpacer(24.dp)
                    }

                    title.invokeAsComposable()
                }
            }
        }
    }
}
