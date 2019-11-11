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
import androidx.ui.core.Px
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.min
import androidx.ui.core.px
import androidx.ui.core.round
import androidx.ui.core.toPx
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.animation.fling
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.DragValueController
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.layout.Container
import androidx.ui.lerp
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.layout.SingleChildLayout

// todo remove once original is useable

// todo use @Model once possible
class ScrollerPosition {

    var value: Px by framed(Px.Zero)
        internal set

    var flingConfigFactory: (Px) -> FlingConfig? by framed {
        FlingConfig(
            decayAnimation = ExponentialDecay(
                frictionMultiplier = ScrollerDefaultFriction,
                absVelocityThreshold = ScrollerVelocityThreshold
            )
        )
    }

    internal var onValueChanged: ((Px) -> Unit)? = null
    internal var onScrollStarted: ((Px) -> Unit)? = null
    internal var onScrollEnded: ((Px, Px) -> Unit)? = null

    private val anim = AnimatedFloat(ScrollPositionValueHolder(0f) {
        onValueChanged?.invoke(-it.px)
    })

    private var dragging = false

    fun smoothScrollTo(
        value: Px,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        anim.animateTo(-value.value, onEnd)
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

    internal val controller = object : DragValueController {
        override val currentValue: Float
            get() = anim.value

        override fun onDrag(target: Float) {
            if (!dragging) {
                dragging = true
                onScrollStarted?.invoke(-target.px)
            }
            anim.snapTo(target)
        }

        override fun onDragEnd(velocity: Float, onValueSettled: (Float) -> Unit) {
            dragging = false
            onScrollEnded?.invoke(velocity.px, -value)
            val flingConfig = flingConfigFactory(velocity.px)
            if (flingConfig != null) {
                val config = flingConfig.copy(
                    onAnimationEnd =
                    { endReason: AnimationEndReason, value: Float, finalVelocity: Float ->
                        if (endReason != AnimationEndReason.Interrupted) onValueSettled(value)
                        flingConfig.onAnimationEnd?.invoke(endReason, value, finalVelocity)
                    })
                anim.fling(config, velocity)
            } else {
                onValueSettled(anim.value)
            }
        }

        override fun setBounds(min: Float, max: Float) {
            anim.setBounds(min, max)
        }
    }

}

@Composable
fun Scroller(
    scrollerPosition: ScrollerPosition = +memo { ScrollerPosition() },
    // todo what to do with the onChangedThing?
    onScrollStarted: ((position: Px) -> Unit)? = null,
    onScrollPositionChanged: (position: Px, maxPosition: Px, viewportSize: Px) -> Unit = { position, _, _ ->
        scrollerPosition.value = position
    },
    onScrollEnded: ((velocity: Px, position: Px) -> Unit)? = null,
    direction: Axis = Axis.Vertical,
    isScrollable: Boolean = true, // todo implement
    maxScrollPosition: Px? = null,
    child: @Composable() () -> Unit
) {
    val maxScrollPositionState = +state { maxScrollPosition ?: Px.Infinity }
    val viewportSize = +state { Px.Zero }

    scrollerPosition.onValueChanged =
        { onScrollPositionChanged(it, maxScrollPositionState.value, viewportSize.value) }
    scrollerPosition.onScrollStarted = onScrollStarted
    scrollerPosition.onScrollEnded = onScrollEnded

    //PressGestureDetector(onPress = { scrollerPosition.scrollTo(scrollerPosition.value) }) {
        Draggable(
            dragDirection = when (direction) {
                Axis.Vertical -> DragDirection.Vertical
                Axis.Horizontal -> DragDirection.Horizontal
            },
            minValue = -maxScrollPositionState.value.value,
            maxValue = 0f,
            valueController = scrollerPosition.controller
        ) {
            ScrollerLayout(
                scrollerPosition = scrollerPosition,
                maxPosition = maxScrollPositionState.value,
                updateMaxPosition = maxScrollPosition == null,
                viewportSize = viewportSize.value,
                onDimensionsChanged = { newMaxScrollPosition, newViewportSize ->
                    if (maxScrollPosition == null) {
                        maxScrollPositionState.value = newMaxScrollPosition
                    }
                    viewportSize.value = newViewportSize
                    onScrollPositionChanged(
                        scrollerPosition.value,
                        newMaxScrollPosition,
                        newViewportSize
                    )
                },
                direction = direction,
                child = child
            )
        }
    //}
}

@Composable
private fun ScrollerLayout(
    scrollerPosition: ScrollerPosition,
    maxPosition: Px,
    updateMaxPosition: Boolean,
    viewportSize: Px,
    onDimensionsChanged: (Px, Px) -> Unit,
    direction: Axis,
    child: @Composable() () -> Unit
) {
    SingleChildLayout(child = {
        Clip(RectangleShape) {
            Container {
                RepaintBoundary(children = child)
            }
        }
    }) { measurable, constraints ->
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
        val placeable = measurable?.measure(childConstraints)
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

            val side = if (updateMaxPosition) {
                when (direction) {
                    Axis.Vertical -> scrollHeight
                    Axis.Horizontal -> scrollWidth
                }
            } else maxPosition
            val newViewportSize = when (direction) {
                Axis.Vertical -> height
                Axis.Horizontal -> width
            }.toPx()
            if (side != maxPosition || newViewportSize != viewportSize) {
                onDimensionsChanged(side, newViewportSize)
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