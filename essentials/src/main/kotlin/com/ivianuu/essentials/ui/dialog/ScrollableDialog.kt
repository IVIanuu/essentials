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

package com.ivianuu.essentials.ui.dialog

import androidx.compose.Composable
import com.ivianuu.essentials.ui.common.ScrollPosition
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.Scroller

@Composable
fun ScrollableDialog(
    scrollPosition: ScrollPosition = retain { ScrollPosition() },
    scrollDirection: Axis = Axis.Vertical,
    scrollingEnabled: Boolean = true,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: @Composable() (() -> Unit)? = null,
    title: @Composable() (() -> Unit)? = null,
    listContent: @Composable() () -> Unit,
    positiveButton: @Composable() (() -> Unit)? = null,
    negativeButton: @Composable() (() -> Unit)? = null,
    neutralButton: @Composable() (() -> Unit)? = null
) {
    MaterialDialog(
        icon = icon,
        title = title,
        // showTopDivider = scrollerPosition.value > scrollerPosition.minValue,
        // showBottomDivider = scrollerPosition.value < scrollerPosition.maxValue,
        applyContentPadding = false,
        buttonLayout = buttonLayout,
        content = {
            Scroller(
                direction = scrollDirection,
                position = scrollPosition,
                enabled = scrollingEnabled
            ) {
                Column {
                    listContent()
                }
            }
        },
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        neutralButton = neutralButton
    )
}
