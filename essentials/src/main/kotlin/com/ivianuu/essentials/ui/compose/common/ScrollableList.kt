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

import androidx.animation.AnimatedFloat
import androidx.animation.AnimationEndReason
import androidx.animation.ExponentialDecay
import androidx.animation.ValueHolder
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.LayoutNode
import androidx.ui.core.Measurable
import androidx.ui.core.ParentData
import androidx.ui.core.Placeable
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
import androidx.ui.foundation.animation.fling
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.DragValueController
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.layout.Column
import androidx.ui.lerp
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.layout.SizedBox
import kotlin.math.max
import kotlin.math.min

// todo direction

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

    PressGestureDetector(onPress = {
        state.controller.onDrag(it.y.value)
    }) {
        Draggable(
            dragDirection = DragDirection.Vertical,
            minValue = -state.maxScroll.value,
            maxValue = 0f,
            valueController = state.controller
        ) {
            ScrollableListLayout(
                { state.offset },
                state.viewportSize,
                {
                    state.viewportSize = it
                    state.onScrollPositionChanged(state.scrollerPosition)
                }
            ) {
                state.itemRange.forEach { index ->
                    composable(index) {
                        ParentData(index) {
                            RepaintBoundary {
                                item(index)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScrollableListLayout(
    offset: () -> Px,
    viewportSize: Px,
    onViewportSizeChanged: (Px) -> Unit,
    children: @Composable() () -> Unit
) = composable("ScrollableListLayout") {
    offset()
    d { "invoke composable" }
    val cachedPlaceables = mutableMapOf<Measurable, Placeable>()
    Layout(children = children) { measureables, constraints ->
        d { "invoke measure" }

        val childConstraints = constraints.copy(
            minWidth = IntPx.Zero,
            minHeight = IntPx.Zero,
            maxHeight = IntPx.Infinity
        )

        val thisLayoutNode = (this as LayoutNode.InnerMeasureScope).layoutNode
        thisLayoutNode.layoutChildren.forEachIndexed { index, layoutNode ->
            d { "child at $index real index ${layoutNode.parentData} needs relayout ${layoutNode.needsRelayout} needs remeasure ${layoutNode.needsRemeasure}" }
        }

        var performedMeasureCount = 0

        val placeables = measureables.map { measureable ->
            val layoutNode = measureable as LayoutNode
            if (layoutNode.needsRemeasure) {
                ++performedMeasureCount
                val placeable = measureable.measure(childConstraints)
                cachedPlaceables[measureable] = placeable
                placeable
            } else {
                cachedPlaceables.getOrPut(measureable) {
                    ++performedMeasureCount
                    measureable.measure(childConstraints)
                }
            }
            //measureable.measure(childConstraints)
        }

        d { "measured $performedMeasureCount children" }

        cachedPlaceables.filterKeys { it !in measureables }
            .forEach {
                cachedPlaceables.remove(it.key)
            }

        layout(constraints.maxWidth, constraints.maxHeight) {
            d { "invoke layout" }

            if (viewportSize != constraints.maxHeight.toPx()) {
                d { "viewport size changed" }
                onViewportSizeChanged(constraints.maxHeight.toPx())
            }
            var offset = -offset()
            placeables.forEach { placeable ->
                placeable.place(Px.Zero, offset)
                offset += placeable.height
            }
        }
    }
}

private class ScrollableListState {

    var count by framed(0)
    var itemSizeProvider: (Int) -> Px by framed { error("") }

    var itemRange by framed(0..0)

    var scrollerPosition by framed(Px.Zero)
    var viewportSize by framed(Px.Zero)
    var offset by framed(Px.Zero)

    // todo compute in update
    val maxScroll: Px
        get() {
            val totalItemsSize = (0 until count)
                .map(itemSizeProvider)
                .fold(Px.Zero) { acc, current -> acc + current }

            return max(Px.Zero, totalItemsSize - viewportSize)
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
                    leading = offset
                )
                offset += size
            }
    }

    private val items = mutableListOf<ItemBounds>()

    val onScrollPositionChanged: (Px) -> Unit = { scrollPosition ->
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

        scrollerPosition = scrollPosition
    }

    private fun computeItemSize(start: Int, end: Int): Px {
        return (start until end)
            .map(itemSizeProvider)
            .fold(Px.Zero) { acc, current -> acc + current }
    }

    private val anim = AnimatedFloat(
        ScrollPositionValueHolder2(
            0f
        ) {
            //onValueChanged?.invoke(-it.px)
            onScrollPositionChanged(-it.px)
        })

    val controller = object : DragValueController {
        override val currentValue: Float
            get() = anim.value

        override fun onDrag(target: Float) {
            anim.snapTo(target)
        }

        override fun onDragEnd(velocity: Float, onValueSettled: (Float) -> Unit) {
            val flingConfig = FlingConfig(
                decayAnimation = ExponentialDecay(
                    frictionMultiplier = ScrollerDefaultFriction,
                    absVelocityThreshold = ScrollerVelocityThreshold
                )
            )

            val config = flingConfig.copy(
                onAnimationEnd =
                { endReason: AnimationEndReason, value: Float, finalVelocity: Float ->
                    if (endReason != AnimationEndReason.Interrupted) onValueSettled(value)
                    flingConfig.onAnimationEnd?.invoke(endReason, value, finalVelocity)
                })
            anim.fling(config, velocity)
        }

        override fun setBounds(min: Float, max: Float) {
            anim.setBounds(min, max)
        }
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
    //scrollPosition >= leading && scrollPosition <= trailing
}

private class ScrollPositionValueHolder2(
    var current: Float,
    val onValueChanged: (Float) -> Unit
) : ValueHolder<Float> {
    override val interpolator: (start: Float, end: Float, fraction: Float) -> Float = ::lerp
    override var value: Float
        get() = current
        set(value) {
            current = value
            onValueChanged(value)
        }
}

private val ScrollerDefaultFriction = 0.35f
private val ScrollerVelocityThreshold = 1000f