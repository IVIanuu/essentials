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
import androidx.compose.unaryPlus
import androidx.ui.core.Px
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.px
import androidx.ui.foundation.animation.FlingConfig
import com.ivianuu.essentials.ui.compose.core.Axis

// todo remove once original is useable
// todo reversed option
// todo rename position and value to offset

// todo use @Model once possible
class ScrollPosition(
    initial: Px = Px.Zero,
    min: Px = Px.Zero,
    max: Px = Px.Infinity
) {

    internal val holder = AnimatedValueHolder(-initial.value)

    val value: Px
        get() = -holder.value.px

    private var _minPosition: Px by framed(min)
    var minPosition: Px
        get() = _minPosition
        set(value) {
            _minPosition = value
            updateBounds()
        }

    private var _maxPosition: Px by framed(max)
    var maxPosition: Px
        get() = _maxPosition
        set(value) {
            _maxPosition = value
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

    private fun updateBounds() {
        holder.setBounds(_minPosition.value, _maxPosition.value)
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
            onDragStarted = { onScrollEvent?.invoke(ScrollEvent.Start, position) },
            onDragValueChangeRequested = {
                position.holder.animatedFloat.snapTo(it)
                onScrollEvent?.invoke(ScrollEvent.Drag, position)
            },
            onDragStopped = {
                position.holder.fling(position.flingConfig, it)
                onScrollEvent?.invoke(ScrollEvent.End, position)
            },
            enabled = enabled,
            children = { child(position) }
        )
    }
}

enum class ScrollEvent {
    Start, Drag, End // todo better names
}

private val ScrollerDefaultFriction = 0.35f
private val ScrollerVelocityThreshold = 1000f