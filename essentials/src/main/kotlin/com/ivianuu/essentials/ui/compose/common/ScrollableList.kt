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

import androidx.animation.ExponentialDecay
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.Px
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.ambientDensity
import androidx.ui.core.coerceIn
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.max
import androidx.ui.core.min
import androidx.ui.core.px
import androidx.ui.core.toPx
import androidx.ui.core.withDensity
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.layout.Column
import com.github.ajalt.timberkt.d
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
    val state = +memo { ScrollableListState() }
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

    PressGestureDetector(onPress = { state.handleDrag(state.holder.value) }) {
        Draggable(
            dragDirection = DragDirection.Vertical,
            dragValue = state.holder,
            onDragValueChangeRequested = { state.handleDrag(it) },
            onDragStopped = { state.handleDragStopped(it) }
        ) {
            ScrollableListLayout(
                state.offset,
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
}

@Composable
private fun ScrollableListLayout(
    offset: Px,
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
            var offset = -offset
            placeables.forEach { placeable ->
                placeable.place(Px.Zero, offset)
                offset += placeable.height
            }
        }
    }
}

private class ScrollableListState {

    val holder = AnimatedValueHolder(0f) {
        handleScrollPositionChanged(-it.px)
    }

    var count by framed(0)
    var itemSizeProvider: (Int) -> Px by framed { error("") }

    var itemRange by framed(0..0)

    val scrollerPosition: Px get() = -holder.animatedFloat.value.px
    var viewportSize by framed(Px.Zero)
    var offset by framed(Px.Zero)

    // todo compute in update
    private var maxScroll = Px.Zero

    fun update() {
        items.clear()

        var offset = Px.Zero

        (0 until count)
            .map(itemSizeProvider)
            .forEachIndexed { index, size ->
                items += ItemBounds(
                    index = index,
                    size = size,
                    leading = offset
                )
                offset += size
            }

        val totalItemsSize = (0 until count)
            .map(itemSizeProvider)
            .fold(Px.Zero) { acc, current -> acc + current }

        maxScroll = max(Px.Zero, totalItemsSize - viewportSize)

        holder.setBounds(-maxScroll.value, 0f)

        handleScrollPositionChanged(scrollerPosition)
    }

    private val items = mutableListOf<ItemBounds>()

    fun handleDrag(scrollPosition: Float) {
        holder.animatedFloat.snapTo(scrollPosition)
    }

    fun handleDragStopped(velocity: Float) {
        holder.fling(
            FlingConfig(
                decayAnimation = ExponentialDecay(
                    frictionMultiplier = ScrollerDefaultFriction,
                    absVelocityThreshold = ScrollerVelocityThreshold
                )
            ), velocity
        )
    }

    fun handleScrollPositionChanged(scrollPosition: Px) {
        val scrollPosition = scrollPosition.coerceIn(Px.Zero, maxScroll)
        if (items.isNotEmpty()) {
            val firstVisibleItem = items.first { it.hitTest(scrollPosition) }

            val firstLayoutIndex = max(0, firstVisibleItem.index)

            val lastVisiblePosition = min(scrollPosition + viewportSize, items.last().trailing)
            val lastVisibleItem = items.last { it.hitTest(lastVisiblePosition) }

            val lastLayoutIndex = min(count - 1, lastVisibleItem.index + 1)

            val sizeUntilFirstLayoutIndex = computeItemSize(0, firstLayoutIndex)
            offset = scrollPosition - sizeUntilFirstLayoutIndex

            d {
                "\nscroller pos $scrollPosition size until first layout $sizeUntilFirstLayoutIndex offset $offset\n" +
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

    private fun computeItemSize(start: Int, end: Int): Px {
        return (start until end)
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
    fun hitTest(scrollPosition: Px): Boolean =
        scrollPosition.value in leading.value..trailing.value
}

private val ScrollerDefaultFriction = 0.35f
private val ScrollerVelocityThreshold = 1000f