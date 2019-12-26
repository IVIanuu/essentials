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

package com.ivianuu.essentials.ui.layout

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.LayoutExpandedWidth
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.core.Axis

@Composable
fun <T> ScrollableList(
    items: List<T>,
    modifier: Modifier = Modifier.None,
    direction: Axis = Axis.Vertical,
    scrollerPosition: ScrollerPosition = remember { ScrollerPosition() },
    enabled: Boolean = true,
    item: @Composable() (Int, T) -> Unit
) {
    ScrollableList(
        modifier = modifier,
        direction = direction,
        scrollerPosition = scrollerPosition,
        enabled = enabled
    ) {
        items.forEachIndexed { index, item ->
            item(index, item)
        }
    }
}

@Composable
fun ScrollableList(
    direction: Axis = Axis.Vertical,
    modifier: Modifier = Modifier.None,
    scrollerPosition: ScrollerPosition = remember { ScrollerPosition() },
    enabled: Boolean = true,
    applyBottomSafeArea: Boolean = true,
    children: @Composable() () -> Unit
) {
    when (direction) {
        Axis.Horizontal -> {
            HorizontalScroller(
                modifier = modifier + LayoutExpandedWidth,
                scrollerPosition = scrollerPosition,
                isScrollable = enabled
            ) {
                Row {
                    children()
                }
            }
        }
        Axis.Vertical -> {
            VerticalScroller(
                modifier = modifier + LayoutExpandedWidth,
                scrollerPosition = scrollerPosition,
                isScrollable = enabled
            ) {
                SafeArea(
                    left = false,
                    top = false,
                    right = false,
                    bottom = applyBottomSafeArea
                ) {
                    Column {
                        children()
                    }
                }
            }
        }
    }
}
