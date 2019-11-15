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
import androidx.compose.ambient
import androidx.compose.compositionReference
import androidx.compose.unaryPlus
import androidx.ui.core.ContextAmbient
import androidx.ui.core.ParentData
import androidx.ui.core.Px
import androidx.ui.core.coerceIn
import androidx.ui.core.ipx
import androidx.ui.core.max
import androidx.ui.core.min
import androidx.ui.core.round
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.subcompose

fun <T> SliverChildren.SliverList(
    items: List<T>,
    itemSize: Px,
    item: @Composable() (Int, T) -> Unit
) = SliverList(
    items = items,
    itemSizeProvider = { _, _ -> itemSize },
    item = item
)

fun <T> SliverChildren.SliverList(
    items: List<T>,
    itemSizeProvider: (Int, SliverConstraints) -> Px,
    item: @Composable() (Int, T) -> Unit
) = SliverList(
    count = items.size,
    itemSizeProvider = itemSizeProvider,
    item = { item(it, items[it]) }
)

fun SliverChildren.SliverList(
    count: Int,
    itemSize: Px,
    item: @Composable() (Int) -> Unit
) = SliverList(
    count = count,
    itemSizeProvider = { _, _ -> itemSize },
    item = item
)

fun SliverChildren.SliverList(
    count: Int,
    itemSizeProvider: (Int, SliverConstraints) -> Px,
    item: @Composable() (Int) -> Unit
) {
    val context = +ambient(ContextAmbient)
    val compositionReference = +compositionReference()

    Sliver(children = {}) { constraints ->
        if (count == 0) return@Sliver layout(SliverGeometry()) {}

        val items = mutableListOf<ItemBounds>()
        var offset = Px.Zero
        (0 until count)
            .map { itemSizeProvider(it, constraints) }
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

        subcompose(context, layoutNode, compositionReference) {
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
        val child = layoutNode.layoutChildren.last()
        child.measure(
            androidx.ui.core.Constraints(
                minWidth = constraints.viewportCrossAxisSpace.round(),
                maxWidth = constraints.viewportCrossAxisSpace.round()
            )
        )

        layout(geometry) {
            d { "child wh ${child.width} ${child.height}" }
            child.place(0.ipx, 0.ipx)
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