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

package com.ivianuu.essentials.sample.ui

import androidx.animation.AnimatedFloat
import androidx.animation.AnimationEndReason
import androidx.animation.ExponentialDecay
import androidx.animation.ValueHolder
import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Constraints
import androidx.ui.core.IntPx
import androidx.ui.core.IntPxPosition
import androidx.ui.core.Layout
import androidx.ui.core.Measurable
import androidx.ui.core.Placeable
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.coerceIn
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.gesture.TouchSlopDragGestureDetector
import androidx.ui.core.ipx
import androidx.ui.core.px
import androidx.ui.core.round
import androidx.ui.core.toPx
import androidx.ui.foundation.animation.AnimatedFloatDragController
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.lerp
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.ref
import kotlin.math.absoluteValue
import kotlin.math.max

@Composable
fun <T> List(
    items: List<T>,
    isVertical: Boolean = true,
    itemBuilder: @Composable() (Int, T) -> Unit
) = composable("List") {
    List(
        count = items.size,
        isVertical = isVertical
    ) { itemBuilder(it, items[it]) }
}

@Composable
fun List(
    count: Int,
    isVertical: Boolean = true,
    itemBuilder: @Composable() (Int) -> Unit
) = composable("List") {
    List(
        isVertical = isVertical
    ) {
        (0 until count).forEach(itemBuilder)
    }
}

@Composable
fun List(
    isVertical: Boolean = true,
    children: @Composable() () -> Unit
) = composable("List") {
    val scrollerPositionState = +state { 0.px }
    val scrollerPosition = +memo { ScrollerPosition(scrollerPositionState) }

    PressGestureDetector(onPress = {
        scrollerPosition.controller.onDrag(scrollerPosition.value.value.value)
    }) {
        TouchSlopDragGestureDetector(
            dragObserver = object : DragObserver {
                override fun onDrag(dragDistance: PxPosition): PxPosition {
                    scrollerPosition.controller.onDrag(scrollerPosition.value.value.value + dragDistance.y.value)
                    // todo horizontal
                    return dragDistance
                }

                override fun onStop(velocity: PxPosition) {
                    scrollerPosition.controller.onDragEnd(velocity.y.value) {}
                }
            }
        ) {
            ListLayout(
                scrollerPosition = scrollerPosition,
                isVertical = isVertical,
                children = children
            )
        }
    }
}

@Composable
private fun ListLayout(
    scrollerPosition: ScrollerPosition,
    isVertical: Boolean,
    children: () -> Unit
) = composable("ListLayout") {
    val childMap = +memo(children) {
        d { "new child map" }
        mutableMapOf<Measurable, ListChild>()
    }
    val maxPositionRef = +ref { Px.Infinity }

    Layout(children) { measurables, constraints ->
        val scrollerPosition = scrollerPosition.value.value
            .coerceIn((-maxPositionRef.value.value).px, 0f.px)

        val currentOffset = scrollerPosition.value.absoluteValue.px

        d { "scroller position $scrollerPosition" }
        d { "max pos ${maxPositionRef.value}" }

        val childConstraints = Constraints(
            maxHeight = if (isVertical) IntPx.Infinity else constraints.maxHeight,
            maxWidth = if (isVertical) constraints.maxWidth else IntPx.Infinity
        )

        val measuredThisTime = mutableSetOf<Measurable>()

        val offsetSize = if (isVertical) constraints.maxHeight else constraints.maxWidth

        var lowerMeasureBound = currentOffset.round() - offsetSize
        if (lowerMeasureBound < 0.ipx) {
            lowerMeasureBound = 0.ipx
        }

        val upperMeasureBound = currentOffset.round() + offsetSize

        d { "lower bound $lowerMeasureBound upper bound $upperMeasureBound" }

        val currentChild = childMap.values.firstOrNull { child ->
            if (isVertical) {
                child.position.y <= currentOffset
                        && child.position.y + child.placeable.height >= currentOffset
            } else {
                child.position.x <= currentOffset
                        && child.position.x + child.placeable.width >= currentOffset
            }
        }
        val minChild = childMap.values.minBy {
            if (isVertical) it.position.y.value else it.position.x.value
        }
        val maxChild = childMap.values.maxBy {
            if (isVertical) it.position.y.value else it.position.x.value
        }

        if (maxChild != null) {
            maxPositionRef.value =
                (maxChild.position.y.toPx() + maxChild.placeable.height) - offsetSize
        }

        d { "max child ${maxChild?.position?.y} scroller position $scrollerPosition offset $currentOffset max pos ref $maxPositionRef" }

        d { "current $currentChild min child $minChild max child $maxChild" }

        // we need more items at the top
        if (minChild != null
            && ((isVertical && minChild.position.y > lowerMeasureBound)
                    || (!isVertical && minChild.position.x > lowerMeasureBound))
        ) {
            var currentMeasured = if (isVertical) minChild.position.y else minChild.position.x
            var currentIndex = measurables.indexOf(minChild.measurable) - 1

            d { "needs more lower bound min child $maxChild, current measured $currentMeasured curr index $currentIndex" }

            while (currentIndex >= 0 && currentMeasured > lowerMeasureBound) {
                val measurable = measurables[currentIndex]
                val placeable = measurable.measure(childConstraints)
                val position = IntPxPosition(
                    x = if (isVertical) 0.ipx else currentMeasured - placeable.width,
                    y = if (isVertical) currentMeasured - placeable.height else 0.ipx
                )
                childMap[measurable] = ListChild(
                    measurable,
                    placeable,
                    position,
                    currentIndex
                )

                d { "lower measured child at $currentIndex" }

                measuredThisTime += measurable
                currentMeasured -= (if (isVertical) placeable.height else placeable.width)
                --currentIndex
            }
        }

        // we need more items at the bottom
        if (maxChild == null
            || (isVertical && maxChild.position.y < upperMeasureBound)
            || (!isVertical && maxChild.position.x < upperMeasureBound)
        ) {
            var currentMeasured = if (maxChild != null) {
                if (isVertical) maxChild.position.y + maxChild.placeable.height
                else maxChild.position.x + maxChild.placeable.width
            } else {
                0.ipx
            }
            var currentIndex = if (maxChild != null && childMap.isNotEmpty()) {
                measurables.indexOf(maxChild.measurable) + 1
            } else {
                0
            }

            d { "needs more upper bound max child $maxChild, current measured $currentMeasured curr index $currentIndex measureables last ${measurables.lastIndex} upper bound $upperMeasureBound" }

            while (currentIndex < measurables.size && currentMeasured < upperMeasureBound) {
                val measurable = measurables[currentIndex]
                val placeable = measurable.measure(childConstraints)
                val position = IntPxPosition(
                    x = if (isVertical) 0.ipx else currentMeasured,
                    y = if (isVertical) currentMeasured else 0.ipx
                )
                childMap[measurable] = ListChild(
                    measurable,
                    placeable,
                    position,
                    currentIndex
                )

                d { "upper measured child at $currentIndex current measured $currentMeasured" }

                measuredThisTime += measurable
                currentMeasured += (if (isVertical) placeable.height else placeable.width)
                ++currentIndex
            }
        }

        // remove out of bounds children
        childMap
            .filterKeys { it !in measuredThisTime }
            .filterValues { child ->
                d { "child " }
                if (isVertical) {
                    child.position.y < lowerMeasureBound || child.position.y > upperMeasureBound
                } else {
                    child.position.x < lowerMeasureBound || child.position.x > upperMeasureBound
                }
            }.forEach { (_, child) ->
                d { "remove child at ${childMap.values.indexOfFirst { it.measurable == child.measurable }}" }
                childMap.remove(child.measurable)
            }

        d { "all children ${childMap.map { it.value.index }.sorted()}" }

        if (childMap.any { it.value.index == measurables.lastIndex }) {
            val overallSize = if (isVertical) {
                childMap.values.sumBy { it.placeable.height.value }
            } else {
                childMap.values.sumBy { it.placeable.width.value }
            }.px

            if (maxPositionRef.value == Px.Infinity) {
                maxPositionRef.value = overallSize
            } else {
                maxPositionRef.value = max(maxPositionRef.value.value, overallSize.value).px
            }
        } else {
            maxPositionRef.value = Px.Infinity
        }

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            childMap.values
                .sortedBy { if (isVertical) it.position.y.value else it.position.x.value }
                .forEach { child ->
                    val position = PxPosition(
                        x = if (isVertical) child.position.x else child.position.x + scrollerPosition.round(),
                        y = if (isVertical) child.position.y + scrollerPosition.round() else child.position.y
                    )
                    child.placeable.place(position)
                }
        }
    }
}

private class ListChild(
    val measurable: Measurable,
    val placeable: Placeable,
    val position: IntPxPosition,
    val index: Int
) {
    override fun toString(): String {
        return "ListChild(position=$position, index=$index)"
    }
}

private inline fun printStacktrace() {
    try {
        error("")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private class ScrollerPosition(val value: State<Px>) {

    private val flingConfig = FlingConfig(
        decayAnimation = ExponentialDecay(
            frictionMultiplier = ScrollerDefaultFriction,
            absVelocityThreshold = ScrollerVelocityThreshold
        )
    )

    internal val controller =
        ScrollerDragValueController({ value.value = it.px }, flingConfig)

    fun smoothScrollTo(
        value: Px,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        controller.animatedFloat.animateTo(-value.value, onEnd)
    }

    fun smoothScrollBy(
        value: Px,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        smoothScrollTo(this.value.value + value, onEnd)
    }

    fun scrollTo(value: Px) {
        controller.onDrag(-value.value)
    }

    fun scrollBy(value: Px) {
        scrollTo(this.value.value + value)
    }

}

private fun ScrollerDragValueController(
    onValueChanged: (Float) -> Unit,
    flingConfig: FlingConfig? = null
) = AnimatedFloatDragController(
    AnimatedFloat(ScrollPositionValueHolder(0f, onValueChanged)),
    flingConfig
)

private class ScrollPositionValueHolder(
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