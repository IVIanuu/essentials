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

package com.ivianuu.essentials.ui.common

import androidx.animation.AnimationBuilder
import androidx.animation.AnimationEndReason
import androidx.animation.ExponentialDecay
import androidx.compose.Composable
import androidx.compose.Stable
import androidx.ui.core.Direction
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.gesture.TouchSlopDragGestureDetector
import androidx.ui.foundation.animation.AnimatedValueHolder
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.unit.Px
import androidx.ui.unit.PxPosition
import androidx.ui.unit.coerceIn
import androidx.ui.unit.px
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.core.Axis

@Stable
class ScrollPosition(
    initial: Px = Px.Zero,
    minValue: Px = Px.Zero,
    maxValue: Px = Px.Infinity
) {

    private val holder =
        AnimatedValueHolder(initial.value)

    val value: Px
        get() = holder.value.px

    private var _minValue: Px by framed(minValue)
    val minValue: Px get() = _minValue

    private var _maxValue: Px by framed(maxValue)
    val maxValue: Px get() = _maxValue

    val isAnimating: Boolean
        get() = holder.animatedFloat.isRunning

    var flingConfigFactory: (Px) -> FlingConfig by framed(
        initial = {
            FlingConfig(
                decayAnimation = ExponentialDecay(
                    frictionMultiplier = ScrollerDefaultFriction,
                    absVelocityThreshold = ScrollerVelocityThreshold
                )
            )
        }
    )

    var direction by framed(ScrollDirection.Idle)

    fun smoothScrollTo(
        value: Px,
        anim: AnimationBuilder<Float>,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        holder.animatedFloat.animateTo(value.value, anim) { endReason, finishValue ->
            onEnd(endReason, finishValue)
            direction = ScrollDirection.Idle
        }
    }

    fun smoothScrollBy(
        value: Px,
        anim: AnimationBuilder<Float>,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        smoothScrollTo(this.value + value, anim) { endReason, finishValue ->
            onEnd(endReason, finishValue)
            direction = ScrollDirection.Idle
        }
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

    fun flingBy(velocity: Px) {
        holder.fling(
            flingConfigFactory(velocity),
            velocity.value
        )
    }

    fun updateBounds(minValue: Px, maxValue: Px) {
        check(minValue <= maxValue) {
            "Min value $minValue cannot be greater than max value $maxValue"
        }
        _minValue = minValue
        _maxValue = maxValue
        holder.setBounds(minValue.value, maxValue.value)
        scrollTo(value.coerceIn(_minValue, _maxValue))

        d { "update bounds $minValue $maxValue" }
    }
}

@Composable
fun Scrollable(
    position: ScrollPosition,
    direction: Axis = Axis.Vertical,
    enabled: Boolean = true,
    child: @Composable() () -> Unit
) {
    PressGestureDetector(onPress = { position.scrollTo(position.value) }) {
        TouchSlopDragGestureDetector(
            dragObserver = object : DragObserver {
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
                    position.direction = scrollDirection
                    position.scrollTo(newValue)

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
                    position.flingBy(mainAxisVelocity)
                }
            },
            canDrag = { dragDirection ->
                if (!enabled) return@TouchSlopDragGestureDetector false
                return@TouchSlopDragGestureDetector when (direction) {
                    Axis.Horizontal -> dragDirection == Direction.LEFT || dragDirection == Direction.RIGHT
                    Axis.Vertical -> dragDirection == Direction.UP || dragDirection == Direction.DOWN
                }
            },
            startDragImmediately = position.isAnimating,
            children = child
        )
    }
}

private const val ScrollerDefaultFriction = 0.35f
private const val ScrollerVelocityThreshold = 1000f

enum class ScrollDirection {
    Idle,
    Forward,
    Reverse
}
