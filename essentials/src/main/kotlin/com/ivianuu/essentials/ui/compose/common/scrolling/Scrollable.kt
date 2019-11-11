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

import androidx.animation.AnimationEndReason
import androidx.animation.ExponentialDecay
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Px
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.px
import androidx.ui.foundation.animation.FlingConfig
import com.ivianuu.essentials.ui.compose.common.AnimatedValueHolder
import com.ivianuu.essentials.ui.compose.common.DragDirection
import com.ivianuu.essentials.ui.compose.common.Draggable
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.core.Axis

// todo remove once original is useable
// todo reversed option

// todo use @Model once possible
class ScrollPosition(
    initialOffset: Px = Px.Zero,
    minOffset: Px = -Px.Infinity,
    maxOffset: Px = Px.Zero
) {

    internal val holder =
        AnimatedValueHolder(initialOffset.value)

    val currentOffset: Px
        get() = holder.value.px

    private var _minOffset: Px by framed(minOffset)
    var minOffset: Px
        get() = _minOffset
        set(value) {
            _minOffset = value
            updateBounds()
        }

    private var _maxOffset: Px by framed(maxOffset)
    var maxOffset: Px
        get() = _maxOffset
        set(value) {
            _maxOffset = value
            updateBounds()
        }

    var flingConfig: FlingConfig by framed(
        FlingConfig(
            decayAnimation = ExponentialDecay(
                frictionMultiplier = ScrollerDefaultFriction,
                absVelocityThreshold = ScrollerVelocityThreshold
            )
        )
    )

    init {
        updateBounds()
    }

    fun smoothScrollTo(
        offset: Px,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        holder.animatedFloat.animateTo(offset.value, onEnd)
    }

    fun smoothScrollBy(
        value: Px,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        smoothScrollTo(currentOffset + value, onEnd)
    }

    fun scrollTo(offset: Px) {
        holder.animatedFloat.snapTo(offset.value)
    }

    fun scrollBy(value: Px) {
        scrollTo(currentOffset + value)
    }

    private fun updateBounds() {
        holder.setBounds(_minOffset.value, _maxOffset.value)
    }
}

@Composable
fun Scrollable(
    position: ScrollPosition = +memo { ScrollPosition() },
    onScrollEvent: ((ScrollEvent, ScrollPosition) -> Unit)? = null,
    direction: Axis = Axis.Vertical,
    reverse: Boolean = false,
    enabled: Boolean = true,
    child: @Composable() (ScrollPosition) -> Unit
) {
    PressGestureDetector(onPress = { position.scrollTo(position.currentOffset) }) {
        Draggable(
            dragDirection = when (direction) {
                Axis.Vertical -> DragDirection.Vertical
                Axis.Horizontal -> DragDirection.Horizontal
            },
            dragValue = position.holder,
            onDragStarted = {
                if (onScrollEvent != null) {
                    onScrollEvent(
                        ScrollEvent.PreStart(
                            position.currentOffset
                        ), position
                    )
                    onScrollEvent(
                        ScrollEvent.Start(
                            position.currentOffset
                        ), position
                    )
                }
            },
            onDragValueChangeRequested = { newOffset ->
                val finalNewOffset =
                    newOffset.coerceIn(position.minOffset.value, position.maxOffset.value)
                val newOffsetPx = finalNewOffset.px
                onScrollEvent?.invoke(
                    ScrollEvent.PreDrag(
                        newOffsetPx
                    ), position
                )
                position.holder.animatedFloat.snapTo(finalNewOffset)
                onScrollEvent?.invoke(
                    ScrollEvent.Drag(
                        newOffsetPx
                    ), position
                )
            },
            onDragStopped = {
                val velocityPx = it.px
                onScrollEvent?.invoke(
                    ScrollEvent.PreEnd(
                        velocityPx
                    ), position
                )
                position.holder.fling(position.flingConfig, it)
                onScrollEvent?.invoke(
                    ScrollEvent.End(
                        velocityPx
                    ), position
                )
            },
            enabled = enabled,
            children = { child(position) }
        )
    }
}

sealed class ScrollEvent {
    data class PreStart(val offset: Px) : ScrollEvent()
    data class Start(val offset: Px) : ScrollEvent()
    data class PreDrag(val offset: Px) : ScrollEvent()
    data class Drag(val offset: Px) : ScrollEvent()
    data class PreEnd(val velocity: Px) : ScrollEvent()
    data class End(val velocity: Px) : ScrollEvent()
    // todo overscroll
}

private val ScrollerDefaultFriction = 0.35f
private val ScrollerVelocityThreshold = 1000f