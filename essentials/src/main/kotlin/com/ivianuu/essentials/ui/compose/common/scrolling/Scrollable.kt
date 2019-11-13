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
import androidx.ui.foundation.animation.AnimatedValueHolder
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.Draggable
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.core.Axis

// todo refactor scroll event handling
// todo reversed option

// todo use @Model once possible
class ScrollPosition(
    initial: Px = Px.Zero,
    minValue: Px = -Px.Infinity,
    maxValue: Px = Px.Zero
) {

    internal val holder =
        AnimatedValueHolder(initial.value)

    val value: Px
        get() = holder.value.px

    private var _minValue: Px by framed(minValue)
    var minValue: Px
        get() = _minValue
        set(value) {
            _minValue = value
            updateBounds()
        }

    private var _maxValue: Px by framed(maxValue)
    var maxValue: Px
        get() = _maxValue
        set(value) {
            _maxValue = value
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

    var direction by framed(ScrollDirection.Idle)

    init {
        updateBounds()
    }

    fun smoothScrollTo(
        value: Px,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        holder.animatedFloat.animateTo(value.value, onEnd)
    }

    fun smoothScrollBy(
        value: Px,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        smoothScrollTo(this.value + value, onEnd)
    }

    fun scrollTo(value: Px) {
        holder.animatedFloat.snapTo(value.value)
    }

    fun scrollBy(value: Px) {
        scrollTo(this.value + value)
    }

    fun correctBy(value: Px) {
        scrollBy(value) // todo check this
    }

    private fun updateBounds() {
        check(_minValue <= _maxValue) {
            "Min value $_minValue cannot be greater than max value $_maxValue"
        }
        holder.setBounds(_minValue.value, _maxValue.value)
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
    PressGestureDetector(onPress = { position.scrollTo(position.value) }) {
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
                            position.value
                        ), position
                    )
                    onScrollEvent(
                        ScrollEvent.Start(
                            position.value
                        ), position
                    )
                }
            },
            onDragValueChangeRequested = { newValue ->
                val finalNewValue =
                    newValue.coerceIn(position.minValue.value, position.maxValue.value)
                position.direction =
                    if (finalNewValue > position.value.value) ScrollDirection.Forward else ScrollDirection.Reverse
                onScrollEvent?.invoke(
                    ScrollEvent.PreDrag(
                        newValue.px
                    ), position
                )
                position.holder.animatedFloat.snapTo(finalNewValue)
                onScrollEvent?.invoke(
                    ScrollEvent.Drag(
                        newValue.px
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
    data class PreStart(val value: Px) : ScrollEvent()
    data class Start(val value: Px) : ScrollEvent()
    data class PreDrag(val value: Px) : ScrollEvent()
    data class Drag(val value: Px) : ScrollEvent()
    data class PreEnd(val velocity: Px) : ScrollEvent()
    data class End(val velocity: Px) : ScrollEvent()
    // todo overscroll
}

private val ScrollerDefaultFriction = 0.35f
private val ScrollerVelocityThreshold = 1000f

enum class ScrollDirection {
    Idle,
    Forward,
    Reverse
}