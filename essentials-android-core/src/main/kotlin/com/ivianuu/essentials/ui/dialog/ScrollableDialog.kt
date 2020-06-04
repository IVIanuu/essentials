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
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller

@Composable
fun HorizontalScrollerDialog(
    scrollerPosition: ScrollerPosition = ScrollerPosition(),
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    scrollerContent: @Composable () -> Unit,
    positiveButton: @Composable (() -> Unit)? = null,
    negativeButton: @Composable (() -> Unit)? = null,
    neutralButton: @Composable (() -> Unit)? = null
) {
    Dialog(
        icon = icon,
        title = title,
        // showTopDivider = scrollerPosition.value > scrollerPosition.minValue,
        // showBottomDivider = scrollerPosition.value < scrollerPosition.maxValue,
        applyContentPadding = false,
        buttonLayout = buttonLayout,
        content = {
            HorizontalScroller(scrollerPosition = scrollerPosition) {
                scrollerContent()
            }
        },
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        neutralButton = neutralButton
    )
}

@Composable
fun VerticalScrollerDialog(
    scrollerPosition: ScrollerPosition = ScrollerPosition(),
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    scrollerContent: @Composable () -> Unit,
    positiveButton: @Composable (() -> Unit)? = null,
    negativeButton: @Composable (() -> Unit)? = null,
    neutralButton: @Composable (() -> Unit)? = null
) {
    Dialog(
        icon = icon,
        title = title,
        // showTopDivider = scrollerPosition.value > scrollerPosition.minValue,
        // showBottomDivider = scrollerPosition.value < scrollerPosition.maxValue,
        applyContentPadding = false,
        buttonLayout = buttonLayout,
        content = {
            VerticalScroller(scrollerPosition = scrollerPosition) {
                scrollerContent()
            }
        },
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        neutralButton = neutralButton
    )
}
