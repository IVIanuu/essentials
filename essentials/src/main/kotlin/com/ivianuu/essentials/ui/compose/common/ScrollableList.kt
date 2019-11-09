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

package com.ivianuu.essentials.ui.compose.common

import androidx.compose.Composable
import androidx.ui.core.Dp
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.layout.SizedBox

@Composable
fun <T> ScrollableList(
    items: List<T>,
    item: @Composable() (Int, T) -> Unit
) = composable("ScrollableList") {
    ScrollableList(items.size) { item(it, items[it]) }
}

@Composable
fun ScrollableList(
    size: Int,
    item: @Composable() (Int) -> Unit
) = composable("ScrollableList") {
    ScrollableList {
        (0 until size).forEach { item(it) }
    }
}

@Composable
fun ScrollableList(
    direction: ScrollDirection = ScrollDirection.Vertical,
    children: @Composable() () -> Unit
) = composable("ScrollableList") {
    when (direction) {
        ScrollDirection.Vertical -> {
            SizedBox(height = Dp.Infinity) {
                VerticalScroller {
                    Column {
                        children()
                    }
                }
            }
        }
        ScrollDirection.Horizontal -> {
            SizedBox(width = Dp.Infinity) {
                HorizontalScroller {
                    Row {
                        children()
                    }
                }
            }
        }
    }
}

enum class ScrollDirection {
    Vertical, Horizontal
}