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

import androidx.animation.AnimationBuilder
import androidx.animation.AnimationEndReason
import androidx.animation.ExponentialDecay
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Direction
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.coerceIn
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.gesture.TouchSlopDragGestureDetector
import androidx.ui.core.px
import androidx.ui.foundation.animation.AnimatedValueHolder
import androidx.ui.foundation.animation.FlingConfig
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.core.Axis

// todo maybe merge with original

// todo refactor scroll event handling
// todo reversed option

// todo use @Model once possible
class ScrollPosition(
    initial: Px = Px.Zero,
    minValue: Px = Px.Zero,
    maxValue: Px = Px.Infinity
) {

    internal val holder =
        AnimatedValueHolder(initial.value)

    val value: Px
        get() = holder.value.px

    private var _minValue: Px by framed(minValue)
    val minValue: Px get() = _minValue

    private var _maxValue: Px by framed(maxValue)
    val maxValue: Px get() = _maxValue

    var flingConfigFactory: (Px) -> FlingConfig by framed {
        FlingConfig(
            decayAnimation = ExponentialDecay(
                frictionMultiplier = ScrollerDefaultFriction,
                absVelocityThreshold = ScrollerVelocityThreshold
            )
        )
    }

    var direction by framed(ScrollDirection.Idle)

    init {
        updateBounds(_minValue, _maxValue)
    }

    fun smoothScrollTo(
        value: Px,
        anim: AnimationBuilder<Float>,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        holder.animatedFloat.animateTo(value.value, anim, onEnd)
    }

    fun smoothScrollBy(
        value: Px,
        anim: AnimationBuilder<Float>,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        smoothScrollTo(this.value + value, anim, onEnd)
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

    fun updateBounds(minValue: Px, maxValue: Px) {
        check(minValue <= maxValue) {
            "Min value $minValue cannot be greater than max value $maxValue"
        }
        _minValue = minValue
        _maxValue = maxValue
        holder.setBounds(minValue.value, maxValue.value)
        d { "update bounds $minValue $maxValue" }
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
        TouchSlopDragGestureDetector(
            dragObserver = object : DragObserver {

                override fun onStart(downPosition: PxPosition) {
                    if (!enabled) return
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
                }

                override fun onDrag(dragDistance: PxPosition): PxPosition {
                    if (!enabled) return PxPosition.Origin
                    val oldValue = position.value
                    val distance = -when (direction) {
                        Axis.Horizontal -> dragDistance.x
                        Axis.Vertical -> dragDistance.y
                    }
                    val newValue =
                        (oldValue + distance).coerceIn(position.minValue, position.maxValue)

                    // todo clean
                    val scrollDirection = when (direction) {
                        Axis.Horizontal -> {
                            if (dragDistance.x <= Px.Zero) ScrollDirection.Forward else ScrollDirection.Reverse
                        }
                        Axis.Vertical -> {
                            if (dragDistance.y <= Px.Zero) ScrollDirection.Forward else ScrollDirection.Reverse
                        }
                    }

                    val consumed = -(newValue - oldValue)
                    d { "orig distance ${dragDistance.x} ${dragDistance.y} old vaue $oldValue -> distance $distance -> new value $newValue direction $scrollDirection consumed $consumed" }
                    position.direction = scrollDirection
                    position.holder.animatedFloat.snapTo(newValue.value)

                    return when (direction) {
                        Axis.Horizontal -> PxPosition(
                            x = consumed,
                            y = Px.Zero
                        )
                        Axis.Vertical -> PxPosition(
                            x = Px.Zero,
                            y = consumed
                        )
                    }
                }

                override fun onStop(velocity: PxPosition) {
                    if (!enabled) return
                    val mainAxisVelocity = -when (direction) {
                        Axis.Horizontal -> velocity.x
                        Axis.Vertical -> velocity.y
                    }
                    onScrollEvent?.invoke(
                        ScrollEvent.PreEnd(
                            mainAxisVelocity
                        ), position
                    )
                    position.holder.fling(
                        position.flingConfigFactory(mainAxisVelocity),
                        mainAxisVelocity.value
                    )
                    onScrollEvent?.invoke(
                        ScrollEvent.End(
                            mainAxisVelocity
                        ), position
                    )
                }
            },
            canDrag = { dragDirection ->
                if (!enabled) return@TouchSlopDragGestureDetector false
                return@TouchSlopDragGestureDetector when (direction) {
                    Axis.Horizontal -> dragDirection == Direction.LEFT || dragDirection == Direction.RIGHT
                    Axis.Vertical -> dragDirection == Direction.UP || dragDirection == Direction.DOWN
                }
            },
            children = {
                child(position)
            }
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