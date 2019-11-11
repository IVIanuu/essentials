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

import androidx.animation.AnimationEndReason
import androidx.animation.ExponentialDecay
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Clip
import androidx.ui.core.IntPx
import androidx.ui.core.Px
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.min
import androidx.ui.core.px
import androidx.ui.core.round
import androidx.ui.core.toPx
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.layout.Container
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.layout.SingleChildLayout

// todo remove once original is useable

// todo use @Model once possible
class ScrollerPosition(initial: Px = Px.Zero) {

    internal val holder = AnimatedValueHolder(-initial.value)

    var maxPosition: Px = Px.Infinity
        internal set

    var viewportSize: Px = Px.Zero
        internal set

    val value: Px
        get() = -holder.value.px

    var flingConfig: FlingConfig by framed(
        FlingConfig(
            decayAnimation = ExponentialDecay(
                frictionMultiplier = ScrollerDefaultFriction,
                absVelocityThreshold = ScrollerVelocityThreshold
            )
        )
    )
    
    fun smoothScrollTo(
        value: Px,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        holder.animatedFloat.animateTo(-value.value, onEnd)
    }

    fun smoothScrollBy(
        value: Px,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        smoothScrollTo(this.value + value, onEnd)
    }

    fun scrollTo(value: Px) {
        holder.animatedFloat.snapTo(-value.value)
    }

    fun scrollBy(value: Px) {
        scrollTo(this.value + value)
    }

}

@Composable
fun Scroller(
    scrollerPosition: ScrollerPosition = +memo { ScrollerPosition() },
    onScrollStarted: ((position: Px) -> Unit)? = null,
    onScrollPositionChanged: ((position: Px, maxPosition: Px, viewportSize: Px) -> Unit)? = null,
    onScrollStopped: ((velocity: Px, position: Px) -> Unit)? = null,
    direction: Axis = Axis.Vertical,
    isScrollable: Boolean = true,
    maxScrollPosition: Px? = null,
    child: @Composable() () -> Unit
) {
    val maxScrollPositionState = +state { maxScrollPosition ?: Px.Infinity }
    val viewportSize = +state { Px.Zero }

    fun updateMaxScrollPosition(maxScrollPosition: Px) {
        scrollerPosition.holder.setBounds(-maxScrollPosition.value, 0f)
        scrollerPosition.maxPosition = maxScrollPosition
        onScrollPositionChanged?.invoke(
            scrollerPosition.value,
            maxScrollPosition,
            scrollerPosition.viewportSize
        )
    }

    +memo(maxScrollPosition) {
        if (maxScrollPosition != null) {
            updateMaxScrollPosition(maxScrollPosition)
        }
    }

    PressGestureDetector(onPress = { scrollerPosition.scrollTo(scrollerPosition.value) }) {
        Draggable(
            dragDirection = when (direction) {
                Axis.Vertical -> DragDirection.Vertical
                Axis.Horizontal -> DragDirection.Horizontal
            },
            dragValue = scrollerPosition.holder,
            onDragStarted = { onScrollStarted?.invoke(scrollerPosition.value) },
            onDragValueChangeRequested = {
                scrollerPosition.holder.animatedFloat.snapTo(it)
                onScrollPositionChanged?.invoke(
                    it.px,
                    scrollerPosition.maxPosition,
                    scrollerPosition.viewportSize
                )
            },
            onDragStopped = {
                scrollerPosition.holder.fling(scrollerPosition.flingConfig, it)
                onScrollStopped?.invoke(it.px, scrollerPosition.value)
            },
            enabled = isScrollable
        ) {
            ScrollerLayout(
                scrollerPosition = scrollerPosition,
                maxPosition = maxScrollPositionState.value,
                updateMaxPosition = maxScrollPosition == null,
                viewportSize = viewportSize.value,
                onDimensionsChanged = { newMaxScrollPosition, newViewportSize ->
                    viewportSize.value = newViewportSize
                    if (maxScrollPosition == null) {
                        updateMaxScrollPosition(newMaxScrollPosition)
                    } else {
                        onScrollPositionChanged?.invoke(
                            scrollerPosition.value,
                            newMaxScrollPosition,
                            newViewportSize
                        )
                    }
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

private val ScrollerDefaultFriction = 0.35f
private val ScrollerVelocityThreshold = 1000f