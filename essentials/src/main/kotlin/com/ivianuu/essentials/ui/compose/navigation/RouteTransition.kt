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

package com.ivianuu.essentials.ui.compose.navigation

import androidx.animation.FloatPropKey
import androidx.animation.TransitionDefinition
import androidx.animation.TransitionState
import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.remember
import androidx.ui.animation.Transition
import androidx.ui.core.Draw
import androidx.ui.core.DrawReceiver
import androidx.ui.core.Opacity
import androidx.ui.core.PxSize
import androidx.ui.graphics.Canvas
import kotlin.time.Duration

@Immutable
data class RouteTransition(
    val definition: @Composable() () -> TransitionDefinition<State>,
    val generateOps: (TransitionState, State) -> Ops
) {

    @Immutable
    interface Type {
        @Composable
        fun apply(
            ops: Ops,
            children: @Composable() () -> Unit
        )
    }

    enum class State {
        Init, Pushed, Popped
    }

    @Immutable
    data class Ops(private val ops: Map<Key<*>, Set<*>>) {
        operator fun <T> get(key: Key<T>): Set<T> = ops.getOrElse(key) { emptySet<T>() } as Set<T>
        class Key<T>
        data class OpWithValue<T>(
            val key: Key<T>,
            val value: T
        )
    }
}

infix fun <T> RouteTransition.Ops.Key<T>.with(value: T): RouteTransition.Ops.OpWithValue<T> =
    RouteTransition.Ops.OpWithValue(this, value)

fun opsOf(
    vararg pairs: RouteTransition.Ops.OpWithValue<*>
): RouteTransition.Ops = RouteTransition.Ops(
    pairs
        .groupBy { it.key }
        .mapValues { (_, value) ->
            value.map { it.value }
        }
        .mapValues { it.value.toSet() }
)

object OpacityRouteTransitionType : RouteTransition.Type {
    val Opacity = RouteTransition.Ops.Key<Float>()
    override fun apply(
        ops: RouteTransition.Ops,
        children: () -> Unit
    ) {
        Opacity(opacity = ops[Opacity].singleOrNull() ?: 1f, children = children)
    }
}

object CanvasRouteTransitionType : RouteTransition.Type {
    val Block = RouteTransition.Ops.Key<DrawReceiver.(Canvas, PxSize) -> Unit>()
    override fun apply(ops: RouteTransition.Ops, children: () -> Unit) {
        Draw(
            children = children,
            onPaint = { canvas, parentSize ->
                ops.get(Block).forEach { it(canvas, parentSize) }
            }
        )
    }
}

val DefaultRouteTransition = RouteTransition(
    definition = { defaultTransitionDefinition },
    generateOps = { _, _ -> opsOf() }
)

private val defaultTransitionDefinition = transitionDefinition {
    state(RouteTransition.State.Init) {}
    state(RouteTransition.State.Pushed) {}
    state(RouteTransition.State.Popped) {}
}

@Composable
internal fun RouteTransitionWrapper(
    transition: RouteTransition,
    state: RouteTransition.State,
    onTransitionComplete: (RouteTransition.State) -> Unit,
    types: List<RouteTransition.Type>,
    children: @Composable() () -> Unit
) {
    Transition(
        definition = transition.definition(),
        toState = state,
        onStateChangeFinished = onTransitionComplete,
        children = { transitionState ->
            val ops = transition.generateOps(transitionState, state)
            RouteTransitionTypes(
                ops = ops,
                types = types,
                children = children
            )
        }
    )
}

@Composable
private fun RouteTransitionTypes(
    ops: RouteTransition.Ops,
    types: List<RouteTransition.Type>,
    children: @Composable() () -> Unit
) {
    types
        .map { type ->
            { typeChildren: @Composable() () -> Unit ->
                type.apply(ops) { typeChildren() }
            }
        }
        .fold({ children() }) { current, type ->
            { type(current) }
        }.invoke()
}

fun FadeRouteTransition(duration: Duration) = RouteTransition(
    definition = {
        remember(duration) {
            fadeRouteTransitionDefinition(duration)
        }
    },
    generateOps = { transitionState, _ ->
        opsOf(
            OpacityRouteTransitionType.Opacity with transitionState[Alpha]
        )
    }
)

private fun fadeRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition<RouteTransition.State> {
    state(RouteTransition.State.Init) { set(Alpha, 0f) }
    state(RouteTransition.State.Pushed) { set(Alpha, 1f) }
    state(RouteTransition.State.Popped) { set(Alpha, 0f) }

    transition(
        RouteTransition.State.Init to RouteTransition.State.Pushed,
        RouteTransition.State.Pushed to RouteTransition.State.Popped
    ) {
        Alpha using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val Alpha = FloatPropKey()

fun VerticalRouteTransition(duration: Duration) = RouteTransition(
    definition = {
        remember(duration) {
            verticalRouteTransitionDefinition(duration)
        }
    },
    generateOps = { transitionState, _ ->
        opsOf(
            CanvasRouteTransitionType.Block with { canvas, parentSize ->
                canvas.save()
                canvas.translate(0f, parentSize.height.value * (1f - transitionState[Progress]))
                drawChildren()
                canvas.restore()
            }
        )
    }
)

private fun verticalRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition<RouteTransition.State> {
    state(RouteTransition.State.Init) { set(Progress, 0f) }
    state(RouteTransition.State.Pushed) { set(Progress, 1f) }
    state(RouteTransition.State.Popped) { set(Progress, 0f) }

    transition(
        RouteTransition.State.Init to RouteTransition.State.Pushed,
        RouteTransition.State.Pushed to RouteTransition.State.Popped
    ) {
        Progress using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val Progress = FloatPropKey()