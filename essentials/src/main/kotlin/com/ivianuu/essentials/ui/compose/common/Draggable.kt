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

import androidx.animation.ValueHolder
import androidx.compose.Composable
import androidx.ui.core.Direction
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.TouchSlopDragGestureDetector
import androidx.ui.core.px
import androidx.ui.foundation.gestures.Draggable
import com.ivianuu.essentials.ui.compose.common.DragDirection.Horizontal
import com.ivianuu.essentials.ui.compose.common.DragDirection.Vertical

// todo remove once in compose

@Composable
fun Draggable(
    dragDirection: DragDirection,
    dragValue: ValueHolder<Float>,
    onDragValueChangeRequested: (Float) -> Unit,
    onDragStarted: (startedPosition: PxPosition) -> Unit = {},
    onDragStopped: (velocity: Float) -> Unit = {},
    enabled: Boolean = true,
    children: @Composable() () -> Unit
) {
    TouchSlopDragGestureDetector(
        dragObserver = object : DragObserver {

            override fun onStart(downPosition: PxPosition) {
                if (enabled) onDragStarted(downPosition)
            }

            override fun onDrag(dragDistance: PxPosition): PxPosition {
                if (!enabled) return PxPosition.Origin
                val oldValue = dragValue.value
                val projected = dragDirection.project(dragDistance)
                onDragValueChangeRequested(oldValue + projected)
                val consumed = dragValue.value - oldValue
                val fractionConsumed = if (projected == 0f) 0f else consumed / projected
                return PxPosition(
                    dragDirection.xProjection(dragDistance.x).px * fractionConsumed,
                    dragDirection.yProjection(dragDistance.y).px * fractionConsumed
                )
            }

            override fun onStop(velocity: PxPosition) {
                if (enabled) onDragStopped(dragDirection.project(velocity))
            }
        },
        canDrag = { direction ->
            enabled && dragDirection
                .isDraggableInDirection(
                    direction,
                    dragValue.value
                )
        },
        children = children
    )
}

/**
 * Draggable Direction specifies the direction in which you can drag an [Draggable].
 * It can be either [Horizontal] or [Vertical].
 */
sealed class DragDirection {

    // TODO: remove internals for children when b/137357249 is ready
    internal abstract val xProjection: (Px) -> Float
    internal abstract val yProjection: (Px) -> Float
    internal abstract val isDraggableInDirection: (
        direction: Direction,
        currentValue: Float
    ) -> Boolean

    internal open fun project(pos: PxPosition) = xProjection(pos.x) + yProjection(pos.y)

    /**
     * Horizontal direction of dragging in [Draggable].
     */
    object Horizontal : DragDirection() {
        internal override val xProjection: (Px) -> Float = { it.value }
        internal override val yProjection: (Px) -> Float = { 0f }
        internal override val isDraggableInDirection:
                    (direction: Direction, currentValue: Float) -> Boolean =
            { direction, _ ->
                when (direction) {
                    Direction.RIGHT -> true
                    Direction.LEFT -> true
                    else -> false
                }
            }
    }

    /**
     * Vertical direction of dragging in [Draggable].
     */
    object Vertical : DragDirection() {
        internal override val xProjection: (Px) -> Float = { 0f }
        internal override val yProjection: (Px) -> Float = { it.value }
        internal override val isDraggableInDirection: (
            direction: Direction,
            currentValue: Float
        ) -> Boolean =
            { direction, _ ->
                when (direction) {
                    Direction.UP -> true
                    Direction.DOWN -> true
                    else -> false
                }
            }
    }
}