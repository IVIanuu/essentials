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
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Clip
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.Px
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.min
import androidx.ui.core.px
import androidx.ui.core.round
import androidx.ui.core.toPx
import androidx.ui.foundation.animation.AnimatedFloatDragController
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.layout.Container
import androidx.ui.lerp
import com.ivianuu.essentials.ui.compose.core.Axis

// todo use @Model once possible
class ScrollerPosition {

    var value: Px by framed(Px.Zero)
        internal set

    internal var onValueChanged: ((Px) -> Unit)? = null

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
        smoothScrollTo(this.value + value, onEnd)
    }

    fun scrollTo(value: Px) {
        controller.onDrag(-value.value)
    }

    fun scrollBy(value: Px) {
        scrollTo(this.value + value)
    }

    // TODO (malkov/tianliu): Open this for customization
    private val flingConfig = FlingConfig(
        decayAnimation = ExponentialDecay(
            frictionMultiplier = ScrollerDefaultFriction,
            absVelocityThreshold = ScrollerVelocityThreshold
        )
    )

    internal val controller =
        ScrollerDragValueController({ onValueChanged?.invoke(-it.px) }, flingConfig)

}

@Composable
fun Scroller(
    scrollerPosition: ScrollerPosition = +memo { ScrollerPosition() },
    onScrollPositionChanged: (position: Px, maxPosition: Px) -> Unit = { position, _ ->
        scrollerPosition.value = position
    },
    direction: Axis = Axis.Vertical,
    isScrollable: Boolean,
    child: @Composable() () -> Unit
) {
    val maxPosition = +state { Px.Infinity }
    scrollerPosition.controller.enabled = isScrollable
    scrollerPosition.onValueChanged = { onScrollPositionChanged(it, maxPosition.value) }
    PressGestureDetector(onPress = { scrollerPosition.scrollTo(scrollerPosition.value) }) {
        Draggable(
            dragDirection = when (direction) {
                Axis.Vertical -> DragDirection.Vertical
                Axis.Horizontal -> DragDirection.Horizontal
            },
            minValue = -maxPosition.value.value,
            maxValue = 0f,
            valueController = scrollerPosition.controller
        ) {
            ScrollerLayout(
                scrollerPosition = scrollerPosition,
                maxPosition = maxPosition.value,
                onMaxPositionChanged = {
                    maxPosition.value = it
                    onScrollPositionChanged(scrollerPosition.value, it)
                },
                direction = direction,
                child = child
            )
        }
    }
}

@Composable
private fun ScrollerLayout(
    scrollerPosition: ScrollerPosition,
    maxPosition: Px,
    onMaxPositionChanged: (Px) -> Unit,
    direction: Axis,
    child: @Composable() () -> Unit
) {
    Layout(children = {
        Clip(RectangleShape) {
            Container {
                RepaintBoundary(children = child)
            }
        }
    }) { measurables, constraints ->
        if (measurables.size > 1) {
            throw IllegalStateException("Only one child is allowed in a VerticalScroller")
        }
        val childConstraints = constraints.copy(
            maxHeight = when (direction) {
                Axis.Vertical -> IntPx.Infinity
                Axis.Horizontal -> constraints.maxHeight
            },
            maxWidth = when (direction) {
                Axis.Vertical -> constraints.maxWidth
                Axis.Horizontal -> IntPx.Infinity
            }
        )
        val childMeasurable = measurables.firstOrNull()
        val placeable = childMeasurable?.measure(childConstraints)
        val width: IntPx
        val height: IntPx
        if (placeable == null) {
            width = constraints.minWidth
            height = constraints.minHeight
        } else {
            width = min(placeable.width, constraints.maxWidth)
            height = min(placeable.height, constraints.maxHeight)
        }
        layout(width, height) {
            val childHeight = placeable?.height?.toPx() ?: 0.px
            val childWidth = placeable?.width?.toPx() ?: 0.px
            val scrollHeight = childHeight - height.toPx()
            val scrollWidth = childWidth - width.toPx()
            val side = when (direction) {
                Axis.Vertical -> scrollHeight
                Axis.Horizontal -> scrollWidth
            }
            if (side != maxPosition) {
                onMaxPositionChanged(side)
            }
            val xOffset = when (direction) {
                Axis.Vertical -> IntPx.Zero
                Axis.Horizontal -> -scrollerPosition.value.round()
            }
            val yOffset = when (direction) {
                Axis.Vertical -> -scrollerPosition.value.round()
                Axis.Horizontal -> IntPx.Zero
            }
            placeable?.place(xOffset, yOffset)
        }
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