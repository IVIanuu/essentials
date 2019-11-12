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
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.Px
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.ambientDensity
import androidx.ui.core.max
import androidx.ui.core.min
import androidx.ui.core.toPx
import androidx.ui.core.withDensity
import androidx.ui.layout.Column
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.layout.SizedBox
import kotlin.math.max
import kotlin.math.min

// todo direction
// todo listener?

@Composable
fun ScrollableList(
    children: @Composable() () -> Unit
) = composable("ScrollableList") {
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
) = composable("ScrollableList") {
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
) = composable("ScrollableList") {
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
) = composable("ScrollableList") {
    ScrollableList(
        count = items.size,
        itemSizeProvider = itemSizeProvider
    ) { item(it, items[it]) }
}

@Composable
fun ScrollableList(
    count: Int,
    itemSizeProvider: (Int) -> Dp,
    item: (Int) -> Unit
) = composable("ScrollableList") {
    /*Scroller {
        Column {
            (0 until count).forEach(item)
        }
    }*/

    Scrollable { position ->
        val state =
            +memo { ScrollableListState(position) }
        +memo(count) { state.count = count }
        val density = +ambientDensity()
        +memo(itemSizeProvider) {
            state.itemSizeProvider = { index: Int ->
                withDensity(density) {
                    itemSizeProvider(index).toPx()
                }
            }
        }

        +memo(count, itemSizeProvider) { state.update() }
        +memo(position.currentOffset) { state.update() }

        ScrollableListLayout(
            state.contentOffset,
            state.viewportSize,
            {
                state.viewportSize = it
                state.update()
            }
        ) {
            state.itemRange.forEach { index ->
                composable(index) {
                    RepaintBoundary {
                        item(index)
                    }
                }
            }
        }
    }
}

private class ScrollableListState(val position: ScrollPosition) {

    var count by framed(0)
    var itemSizeProvider: (Int) -> Px by framed { error("") }

    private val items = mutableListOf<ItemBounds>()
    var itemRange by framed(0..0)

    var contentOffset by framed(Px.Zero)

    var viewportSize = Px.Zero
        set(value) {
            field = value
            update()
        }

    fun update() {
        items.clear()

        var offset = Px.Zero

        (0 until count)
            .map(itemSizeProvider)
            .forEachIndexed { index, size ->
                items += ItemBounds(
                    index = index,
                    size = size,
                    leading = offset - size,
                    trailing = offset
                )
                offset -= size
            }

        val newMinOffset = min(Px.Zero, offset + viewportSize)
        if (position.minOffset != newMinOffset) {
            position.minOffset = newMinOffset
        }

        onScrollOffsetChanged()
    }

    fun onScrollOffsetChanged() {
        val scrollOffset = position.currentOffset
        if (items.isNotEmpty()) {
            val firstVisibleItem = items.last { it.hitTest(scrollOffset) }

            val firstLayoutIndex = max(0, firstVisibleItem.index)

            val lastVisiblePosition = max(scrollOffset - viewportSize, items.last().trailing)
            val lastVisibleItem = items.last { it.hitTest(lastVisiblePosition) }

            val lastLayoutIndex = min(count - 1, lastVisibleItem.index + 1)

            val sizeUntilFirstLayoutIndex = computeItemSize(0, firstLayoutIndex)
            contentOffset = scrollOffset + sizeUntilFirstLayoutIndex

            d {
                "\nscroll offset $scrollOffset size until first layout $sizeUntilFirstLayoutIndex contentOffset $contentOffset\n" +
                        "visible range ${firstVisibleItem.index..lastVisibleItem.index}\n" +
                        "layout range ${firstLayoutIndex..lastLayoutIndex}\n" +
                        "total size $count"
            }

            if (itemRange.first != firstLayoutIndex || itemRange.last != lastLayoutIndex) {
                itemRange = firstLayoutIndex..lastLayoutIndex
            }
        } else {
            if (itemRange.first != 0 || itemRange.last != 0) {
                itemRange = 0..0
            }
        }
    }

    private fun computeItemSize(startIndex: Int, endIndex: Int): Px {
        return (startIndex until endIndex)
            .map(itemSizeProvider)
            .fold(Px.Zero) { acc, current -> acc + current }
    }

}

private data class ItemBounds(
    val index: Int,
    val size: Px,
    val leading: Px,
    val trailing: Px = leading + size
) {
    // todo avoid alloc
    fun hitTest(scrollPosition: Px): Boolean =
        scrollPosition.value in leading.value..trailing.value
}

@Composable
private fun ScrollableListLayout(
    contentOffset: Px,
    viewportSize: Px,
    onViewportSizeChanged: (Px) -> Unit,
    children: @Composable() () -> Unit
) = composable("ScrollableListLayout") {
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
            var offset = contentOffset
            placeables.forEach { placeable ->
                placeable.place(Px.Zero, offset)
                offset += placeable.height
            }
        }
    }
}