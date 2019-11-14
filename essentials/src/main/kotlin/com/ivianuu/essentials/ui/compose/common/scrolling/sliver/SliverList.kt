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

package com.ivianuu.essentials.ui.compose.common.scrolling.sliver

import androidx.compose.Composable
import androidx.ui.core.Dp
import androidx.ui.core.ParentData
import androidx.ui.core.Px
import androidx.ui.core.coerceIn
import androidx.ui.core.max
import androidx.ui.core.min
import androidx.ui.core.round
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.composable

fun <T> SliverChildren.SliverList(
    items: List<T>,
    itemSize: Dp,
    item: @Composable() (Int, T) -> Unit
) = SliverList(
    items = items,
    itemSizeProvider = { itemSize },
    item = item
)

fun <T> SliverChildren.SliverList(
    items: List<T>,
    itemSizeProvider: (Int) -> Dp,
    item: @Composable() (Int, T) -> Unit
) = SliverList(
    count = items.size,
    itemSizeProvider = itemSizeProvider,
    item = { item(it, items[it]) }
)

fun SliverChildren.SliverList(
    count: Int,
    itemSize: Dp,
    item: @Composable() (Int) -> Unit
) = SliverList(
    count = count,
    itemSizeProvider = { itemSize },
    item = item
)

fun SliverChildren.SliverList(
    count: Int,
    itemSizeProvider: (Int) -> Dp,
    item: @Composable() (Int) -> Unit
) = Sliver { constraints ->
    if (count == 0) return@Sliver content(SliverGeometry()) {}

    val items = mutableListOf<ItemBounds>()
    var offset = Px.Zero
    (0 until count)
        .map(itemSizeProvider)
        .map { it.toPx() }
        .mapIndexed { index, size ->
            items += ItemBounds(
                index = index,
                size = size,
                leading = offset,
                trailing = offset + size
            )
            offset += size
        }

    var totalScrollSize = Px.Zero
    items.forEach { totalScrollSize += it.size }

    val scrollOffset = max(constraints.scrollPosition, Px.Zero)

    val itemRange: IntRange? = if (scrollOffset <= totalScrollSize) {
        val firstChild = items.first { it.hitTest(scrollOffset) }
        val lastChild = items.first {
            it.hitTest(
                min(
                    scrollOffset + constraints.remainingPaintSpace,
                    items.last().trailing
                )
            )
        }

        firstChild.index..lastChild.index
    } else {
        null
    }

    val paintSize = if (itemRange != null) {
        calculatePaintSize(
            constraints,
            from = items[itemRange.first].leading,
            to = items[itemRange.last].trailing
        )
    } else Px.Zero

    d {
        "layout\n" +
                "scroll offset $scrollOffset" +
                "item range $itemRange\n" +
                "constraints $constraints" +
                "\npaint size $paintSize"
    }

    val geometry = SliverGeometry(
        scrollSize = totalScrollSize,
        paintSize = paintSize,
        maxPaintSize = totalScrollSize
    )

    content(geometry = geometry) {
        SliverChildLayout(constraints = constraints, geometry = geometry) {
            itemRange
                ?.map { items[it] }
                ?.forEach { item ->
                    composable(item.index) {
                        ParentData(
                            SliverChildParentData(
                                size = item.size.round(),
                                layoutOffset = item.leading
                            )
                        ) {
                            item(item.index)
                        }
                    }
                }
        }
    }
}

private fun calculatePaintSize(
    constraints: SliverConstraints,
    from: Px,
    to: Px
): Px {
    val a = constraints.scrollPosition
    val b = constraints.scrollPosition + constraints.remainingPaintSpace
    return (to.coerceIn(a, b) - from.coerceIn(a, b)).coerceIn(
        Px.Zero,
        constraints.remainingPaintSpace
    )
}

private data class ItemBounds(
    val index: Int,
    val size: Px,
    val leading: Px,
    val trailing: Px = leading + size
) {
    fun hitTest(scrollPosition: Px): Boolean =
        scrollPosition >= leading && scrollPosition <= trailing
}