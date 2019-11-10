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
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable

// todo is this a good name?

// todo add PagerPosition class

// todo use page instead of item for name

@Composable
fun <T> Pager(
    items: List<T>,
    currentPage: Int,
    direction: Axis = Axis.Vertical,
    item: @Composable() (Int, T) -> Unit
) = composable("Pager") {
    Pager(
        size = items.size,
        currentPage = currentPage,
        direction = direction
    ) {
        item(it, items[it])
    }
}

@Composable
fun Pager(
    size: Int,
    currentPage: Int,
    direction: Axis = Axis.Vertical,
    item: @Composable() (Int) -> Unit
) = composable("Pager") {
    PagerLayout(
        size = size,
        currentPage = currentPage,
        direction = direction,
        item = item
    )
}

@Composable
private fun PagerLayout(
    size: Int,
    currentPage: Int,
    direction: Axis,
    item: @Composable() (Int) -> Unit
) = composable("PagerLayout") {
    Layout({
        (0 until size).forEach(item)
    }) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = constraints.maxWidth,
            minHeight = constraints.maxHeight
        )
        val placeables = measureables.map {
            it.measure(childConstraints)
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            val currentPageOffset = when (direction) {
                Axis.Vertical -> constraints.maxHeight * currentPage
                Axis.Horizontal -> constraints.maxWidth * currentPage
            }

            var offset = IntPx.Zero
            placeables.forEachIndexed { index, placeable ->
                when (direction) {
                    Axis.Vertical -> {
                        placeable.place(
                            IntPx.Zero,
                            offset - currentPageOffset
                        )

                        offset += placeable.height
                    }
                    Axis.Horizontal -> {
                        placeable.place(
                            offset - currentPageOffset,
                            IntPx.Zero
                        )

                        offset += placeable.width
                    }
                }
            }
        }
    }
}