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
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Direction
import androidx.ui.core.Dp
import androidx.ui.core.Measurable
import androidx.ui.core.ParentData
import androidx.ui.core.Placeable
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.max
import androidx.ui.core.min
import androidx.ui.core.px
import androidx.ui.core.toPx
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.toAxis
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

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
    itemSizeProvider: (Int) -> Dp,
    item: @Composable() (Int) -> Unit
) {
    val state = +memo { SliverListState() }
    state.count = count
    state.onCompose()

    Sliver(
        children = {
            if (state.itemRange != emptyItemRange) {
                d { "compose range ${state.itemRange}" }
                state.itemRange.forEach { index ->
                    composable(index) {
                        ParentData(IndexedParentData(index)) {
                            item(index)
                        }
                    }
                }
            }
        }
    ) { measureables, constraints ->
        if (measureables.isEmpty()) return@Sliver layout(SliverGeometry.Zero) {}

        val leadingMeasureable = if (state.initialLayout) {
            measureables.first()
        } else {
            measureables.first { it.listIndex == state.leadingItem.index }
        }

        val layoutItems = mutableListOf<SliverLayoutItem>()
        val childConstraints = constraints.asConstraints()

        val leadingPlaceable = leadingMeasureable.measure(childConstraints)
        val leadingItem = if (state.initialLayout) {
            ItemInfo(
                leadingMeasureable.listIndex,
                Px.Zero,
                leadingPlaceable.height.toPx()
            )
        } else {
            state.leadingItem
        }

        if (state.initialLayout) {
            state.initialLayout = false
            state.leadingItem = leadingItem
        }

        layoutItems += SliverLayoutItem(
            index = leadingMeasureable.listIndex,
            measurable = leadingMeasureable,
            placeable = leadingPlaceable,
            leading = leadingItem.leading,
            trailing = leadingItem.trailing
        )

        // up
        var toFill = max(
            constraints.cacheOrigin.value.absoluteValue.px,
            leadingItem.leading - constraints.scrollPosition
        )
        var currentIndex = measureables.indexOf(leadingMeasureable) - 1
        var currentOffset = leadingItem.leading
        while (currentIndex in measureables.indices && toFill > Px.Zero) {
            val currentMeasureable = measureables[currentIndex]
            val placeable = currentMeasureable.measure(childConstraints)
            val leading = currentOffset - placeable.height.toPx()
            val trailing = currentOffset

            layoutItems += SliverLayoutItem(
                index = currentMeasureable.listIndex,
                measurable = currentMeasureable,
                placeable = placeable,
                leading = leading,
                trailing = trailing
            )

            toFill -= placeable.height.toPx()
            currentOffset = leading
            currentIndex -= 1
        }

        // down
        toFill = constraints.remainingCacheSpace
        currentIndex = measureables.indexOf(leadingMeasureable) + 1
        currentOffset = leadingItem.trailing
        while (currentIndex in measureables.indices && toFill > Px.Zero) {
            val currentMeasureable = measureables[currentIndex]
            val placeable = currentMeasureable.measure(childConstraints)
            val leading = currentOffset
            val trailing = leading + placeable.height.toPx()

            layoutItems += SliverLayoutItem(
                index = currentMeasureable.listIndex,
                measurable = currentMeasureable,
                placeable = placeable,
                leading = leading,
                trailing = trailing
            )

            toFill -= placeable.height.toPx()
            currentOffset = trailing
            currentIndex += 1
        }

        d {
            "measureables size ${measureables.size} " +
                    "leading item ${state.leadingItem} " +
                    "layout items size ${layoutItems.size} " +
                    "layout items ${layoutItems.map { it.index to (it.leading to it.trailing) }} " +
                    "scroll position ${constraints.scrollPosition}"
        }

        val trailingLayoutItem = layoutItems.last()
        val newTrailingInfo = ItemInfo(
            index = trailingLayoutItem.index,
            leading = trailingLayoutItem.leading,
            trailing = trailingLayoutItem.trailing
        )
        if (newTrailingInfo != state.trailingItem) {
            d { "new trailing item is $newTrailingInfo" }
            state.trailingItem = newTrailingInfo
        }

        val scrollPosition = min(newTrailingInfo.trailing, constraints.scrollPosition)

        if (!leadingItem.hitTest(scrollPosition)) {
            val newItemInfo = layoutItems.first { item ->
                scrollPosition >= item.leading
                        && scrollPosition <= item.trailing
            }

            state.leadingItem = ItemInfo(
                index = newItemInfo.index,
                leading = newItemInfo.leading,
                trailing = newItemInfo.trailing
            )

            d { "new leading item is ${state.leadingItem} scroll position ${constraints.scrollPosition} layout item $newItemInfo" }
        }

        val geometry = if (state.trailingItem.index == count - 1) {
            SliverGeometry(
                scrollSize = state.trailingItem.trailing,
                paintSize = constraints.remainingPaintSpace,
                maxPaintSize = constraints.remainingPaintSpace
            )
        } else {
            SliverGeometry(
                scrollSize = Px.Infinity,
                paintSize = constraints.remainingPaintSpace,
                maxPaintSize = Px.Infinity
            )
        }

        state.onCompose()

        layout(geometry) {
            val mainAxisUnit: PxPosition
            val crossAxisUnit: PxPosition
            val originOffset: PxPosition
            val addSize: Boolean

            when (constraints.mainAxisDirection.applyGrowthDirection(constraints.growthDirection)) {
                Direction.LEFT -> {
                    mainAxisUnit = PxPosition((-1).px, Px.Zero)
                    crossAxisUnit = PxPosition(Px.Zero, 1.px)
                    originOffset = PxPosition(geometry.paintSize, Px.Zero)
                    addSize = true
                }
                Direction.UP -> {
                    mainAxisUnit = PxPosition(Px.Zero, (-1).px)
                    crossAxisUnit = PxPosition(1.px, Px.Zero)
                    originOffset = PxPosition(Px.Zero, geometry.paintSize)
                    addSize = true
                }
                Direction.RIGHT -> {
                    mainAxisUnit = PxPosition(1.px, Px.Zero)
                    crossAxisUnit = PxPosition(Px.Zero, 1.px)
                    originOffset = PxPosition.Origin
                    addSize = false
                }
                Direction.DOWN -> {
                    mainAxisUnit = PxPosition(Px.Zero, 1.px)
                    crossAxisUnit = PxPosition(1.px, Px.Zero)
                    originOffset = PxPosition.Origin
                    addSize = false
                }
            }

            layoutItems.forEach { item ->
                val mainAxisDelta = item.leading - constraints.scrollPosition
                val crossAxisDelta = Px.Zero
                var childOffset = PxPosition(
                    x = (originOffset.x.value + mainAxisUnit.x.value * mainAxisDelta.value + crossAxisUnit.x.value * crossAxisDelta.value).px,
                    y = (originOffset.y.value + mainAxisUnit.y.value * mainAxisDelta.value + crossAxisUnit.y.value * crossAxisDelta.value).px
                )

                val paintSize = when (constraints.mainAxisDirection.toAxis()) {
                    Axis.Horizontal -> item.placeable.width
                    Axis.Vertical -> item.placeable.height
                }

                if (addSize) {
                    childOffset = PxPosition(
                        x = childOffset.x + (mainAxisUnit.x.value * paintSize.value).px,
                        y = childOffset.y + (mainAxisUnit.y.value * paintSize.value).px
                    )
                }

                //if (mainAxisDelta < constraints.remainingPaintSpace && mainAxisDelta + paintSize > Px.Zero) {
                    item.placeable.place(childOffset)
                //}
            }
        }
    }
}

private class SliverListState {

    var count = 0
    var itemRange by framed(emptyItemRange)

    var initialLayout = true

    var leadingItem = ItemInfo.Unknown
    var trailingItem by framed(ItemInfo.Unknown)

    fun onCompose() {
        val newItemRange = if (count == 0) {
            leadingItem = ItemInfo.Unknown
            trailingItem = ItemInfo.Unknown
            initialLayout = true
            emptyItemRange
        } else if (initialLayout) {
            (0 until min(count, 20))
        } else {
            val first = max(0, leadingItem.index - 50)
            val last = min(count, leadingItem.index + 50)
            first until last
        } //(0 until count)

        if (newItemRange != itemRange) {
            itemRange = newItemRange
        }
    }

}

private val emptyItemRange = (-2..-1)

private data class IndexedParentData(val index: Int)

private val Measurable.listIndex: Int get() = (parentData as IndexedParentData).index

private data class ItemInfo(
    val index: Int,
    val leading: Px,
    val trailing: Px
) {

    fun hitTest(position: Px) =
        (position >= leading && position <= trailing)

    companion object {
        val Unknown = ItemInfo(-1, Px.Zero, Px.Zero)
    }
}

private data class SliverLayoutItem(
    val index: Int,
    val measurable: Measurable,
    val placeable: Placeable,
    val leading: Px,
    val trailing: Px
)
