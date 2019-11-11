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
import androidx.ui.core.Px
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.px
import androidx.ui.core.toPx
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.animation.fling
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.DragValueController
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.lerp
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.withDensity
import kotlin.math.max
import kotlin.math.min

@Composable
fun <T> ScrollableList2(
    items: List<T>,
    itemSize: Dp,
    item: (Int, T) -> Unit
) = composable("ScrollableList") {
    ScrollableList2(
        count = items.size,
        itemSize = itemSize
    ) { item(it, items[it]) }
}

@Composable
fun ScrollableList2(
    count: Int,
    itemSize: Dp,
    item: (Int) -> Unit
) = composable("ScrollableList") {
    val state = +memo { ScrollableListState() }
    state.count = count
    state.itemSize = +withDensity { itemSize.toPx() }

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
                state.offset,
                state.viewportSize,
                {
                    state.viewportSize = it
                    state.onScrollPositionChanged(state.scrollerPosition)
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
            d { "layout placeable size ${placeables.size}" }
            placeables.forEach { placeable ->
                placeable.place(Px.Zero, offset)
                offset += placeable.height
            }
        }
    }
}

private class ScrollableListState {

    var count by framed(0)
    var itemSize by framed(Px.Zero)

    var itemRange by framed(0..20)

    var scrollerPosition by framed(Px.Zero)
    var viewportSize by framed(Px.Zero)
    var offset by framed(Px.Zero)

    val layoutOffset: Px get() = Px.Zero
    val maxScroll: Px get() = (itemSize * count) - viewportSize

    val onScrollPositionChanged: (Px) -> Unit = { scrollPosition ->
        val layoutOffsetItemCount = (layoutOffset / itemSize).toInt()

        val firstVisibleIndex = (scrollPosition / itemSize).toInt()

        val firstLayoutIndex = max(0, firstVisibleIndex - layoutOffsetItemCount)

        val lastVisibleIndex = ((scrollPosition + viewportSize) / itemSize).toInt()

        val lastLayoutIndex = min(count - 1, lastVisibleIndex + layoutOffsetItemCount)

        offset = scrollPosition - (itemSize * firstLayoutIndex)

        d {
            "scroller pos $scrollPosition offset $offset\n" +
                    "visible range ${firstVisibleIndex..lastVisibleIndex}\n" +
                    "layout range ${firstLayoutIndex..lastLayoutIndex}\n" +
                    "total size $count"
        }

        if (itemRange.first != firstLayoutIndex || itemRange.last != lastLayoutIndex) {
            itemRange = firstLayoutIndex..lastLayoutIndex
        }

        scrollerPosition = scrollPosition
    }

    private val anim = AnimatedFloat(ScrollPositionValueHolder2(0f) {
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