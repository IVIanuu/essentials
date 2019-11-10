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
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.layout.SizedBox

@Composable
fun <T> ScrollableList(
    items: List<T>,
    direction: Axis = Axis.Vertical,
    item: @Composable() (Int, T) -> Unit
) = composable("ScrollableList") {
    ScrollableList(size = items.size, direction = direction) { item(it, items[it]) }
}

@Composable
fun ScrollableList(
    size: Int,
    direction: Axis = Axis.Vertical,
    item: @Composable() (Int) -> Unit
) = composable("ScrollableList") {
    ScrollableList(direction = direction) {
        (0 until size).forEach { item(it) }
    }
}

// todo rename
@Composable
fun ScrollableList(
    direction: Axis = Axis.Vertical,
    children: @Composable() () -> Unit
) = composable("ScrollableList") {
    when (direction) {
        Axis.Vertical -> {
            SizedBox(height = Dp.Infinity) {
                VerticalScroller {
                    Column {
                        children()
                    }
                }
            }
        }
        Axis.Horizontal -> {
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