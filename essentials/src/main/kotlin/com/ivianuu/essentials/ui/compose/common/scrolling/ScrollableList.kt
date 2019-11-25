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

package com.ivianuu.essentials.ui.compose.common.scrolling

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Clip
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.Px
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.ambientDensity
import androidx.ui.core.max
import androidx.ui.core.toPx
import androidx.ui.core.withDensity
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.essentials.ui.compose.layout.SizedBox
import kotlin.math.max
import kotlin.math.min

// todo customization
// todo enabled
// todo reverse
// todo direction

@Composable
fun ScrollableList(
    children: @Composable() () -> Unit
) = composable {
    SizedBox(height = Dp.Infinity) {
        Scroller {
            Column {
                children()
            }
        }
    }
}

@Composable
fun <T> ScrollableList(
    items: List<T>,
    itemSize: Dp,
    item: @Composable() (Int, T) -> Unit
) = composable {
    ScrollableList(
        count = items.size,
        itemSizeProvider = { itemSize }
    ) { item(it, items[it]) }
}

@Composable
fun ScrollableList(
    count: Int,
    itemSize: Dp,
    item: @Composable() (Int) -> Unit
) = composable {
    ScrollableList(
        count = count,
        itemSizeProvider = { itemSize },
        item = item
    )
}

@Composable
fun <T> ScrollableList(
    items: List<T>,
    itemSizeProvider: (Int) -> Dp,
    item: @Composable() (Int, T) -> Unit
) = composable {
    ScrollableList(
        count = items.size,
        itemSizeProvider = itemSizeProvider
    ) { item(it, items[it]) }
}

@Composable
fun ScrollableList(
    count: Int,
    itemSizeProvider: (Int) -> Dp,
    item: @Composable() (Int) -> Unit
) = composable {
    val state = memo { ScrollableListState(ScrollPosition()) }
    memo(count) { state.count = count }
    val density = ambientDensity()()
    memo(itemSizeProvider) {
        state.itemSizeProvider = { index: Int ->
            withDensity(density) {
                itemSizeProvider(index).toPx()
            }
        }
    }

    memo(count, itemSizeProvider) { state.itemsChanged() }

    Scrollable(position = state.position) {
        memo(state.position.value) { state.computeVisibleItemRange() }

        Clip(RectangleShape) {
            Container(alignment = Alignment.TopLeft) {
                RepaintBoundary {
                    ScrollableListLayout(
                        state.contentOffset,
                        state.viewportSize,
                        { state.viewportSize = it }
                    ) {
                        if (state.itemRange != emptyItemRange) {
                            state.itemRange.forEach { index ->
                                composableWithKey(index) {
                                    item(index)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// @Model
private class ScrollableListState(val position: ScrollPosition) {

    var count by framed(0)
    var itemSizeProvider: (Int) -> Px by framed { index: Int -> error("") }

    private val items = mutableListOf<ItemInfo>()
    var itemRange by framed(emptyItemRange)

    var contentOffset by framed(Px.Zero)

    var viewportSize = Px.Zero
        set(value) {
            field = value
            viewportSizeChanged()
        }

    fun itemsChanged() {
        items.clear()

        if (count != 0) {
            var offset = Px.Zero

            for (index in 0 until count) {
                val size = itemSizeProvider(index)
                items += ItemInfo(
                    index = index,
                    size = size,
                    leading = offset,
                    trailing = offset + size
                )
                offset += size
            }
        }

        viewportSizeChanged()
    }

    fun viewportSizeChanged() {
        val newMaxValue = max(Px.Zero, (items.lastOrNull()?.trailing ?: Px.Zero) - viewportSize)
        if (position.maxValue != newMaxValue) {
            position.updateBounds(Px.Zero, newMaxValue)
        }

        computeVisibleItemRange()
    }

    fun computeVisibleItemRange() {
        val scrollPosition = position.value
        val newItemRange = if (count == 0) {
            emptyItemRange
        } else {
            val firstVisibleItem = items.last { it.hitTest(scrollPosition) }

            val firstLayoutIndex = max(0, firstVisibleItem.index)

            val lastVisiblePosition = max(scrollPosition - viewportSize, items.last().trailing)
            val lastVisibleItem = items.last { it.hitTest(lastVisiblePosition) }

            val lastLayoutIndex = min(count - 1, lastVisibleItem.index + 1)

            contentOffset = scrollPosition - firstVisibleItem.leading

            firstLayoutIndex..lastLayoutIndex
        }

        if (newItemRange != itemRange) {
            itemRange = newItemRange
        }
    }

}

private val emptyItemRange = -2..-1

private data class ItemInfo(
    val index: Int,
    val size: Px,
    val leading: Px,
    val trailing: Px = leading + size
) {
    fun hitTest(scrollPosition: Px): Boolean =
        scrollPosition >= leading && scrollPosition <= trailing
}

@Composable
private fun ScrollableListLayout(
    contentOffset: Px,
    viewportSize: Px,
    onViewportSizeChanged: (Px) -> Unit,
    children: @Composable() () -> Unit
) = composable {
    Layout(children = children) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = IntPx.Zero,
            minHeight = IntPx.Zero,
            maxHeight = IntPx.Infinity
        )

        val placeables = measureables.map { measureable ->
            measureable.measure(childConstraints)
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            if (viewportSize != constraints.maxHeight.toPx()) {
                onViewportSizeChanged(constraints.maxHeight.toPx())
            }
            var offset = -contentOffset
            placeables.forEach { placeable ->
                placeable.place(Px.Zero, offset)
                offset += placeable.height
            }
        }
    }
}