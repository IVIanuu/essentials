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

package com.ivianuu.essentials.ui.navigation

import androidx.animation.TransitionDefinition
import androidx.animation.TransitionState
import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.staticAmbientOf
import androidx.ui.animation.Transition
import androidx.ui.core.Alignment
import androidx.ui.core.Draw
import androidx.ui.core.DrawReceiver
import androidx.ui.core.Modifier
import androidx.ui.core.Opacity
import androidx.ui.graphics.Canvas
import androidx.ui.layout.Container
import androidx.ui.unit.PxSize

@Immutable
class RouteTransition(
    val definition: @Composable () -> TransitionDefinition<State>,
    val generateOps: (TransitionState, State) -> Ops
) {
    @Immutable
    interface Type {
        @Composable
        fun apply(
            ops: Ops,
            children: @Composable () -> Unit
        )
    }

    enum class State {
        Init,
        EnterFromPush,
        ExitFromPush,
        ExitFromPop,
        EnterFromPop
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
    @Composable
    override fun apply(
        ops: RouteTransition.Ops,
        children: @Composable () -> Unit
    ) {
        Opacity(opacity = ops[Opacity].singleOrNull() ?: 1f, children = children)
    }
}

object CanvasRouteTransitionType : RouteTransition.Type {
    val Block = RouteTransition.Ops.Key<DrawReceiver.(Canvas, PxSize) -> Unit>()
    @Composable
    override fun apply(ops: RouteTransition.Ops, children: @Composable () -> Unit) {
        Draw(
            children = children,
            onPaint = { canvas, parentSize ->
                ops[Block].forEach { it(canvas, parentSize) }
            }
        )
    }
}

object ModifierRouteTransitionType : RouteTransition.Type {
    val Modifier = RouteTransition.Ops.Key<Modifier>()
    @Composable
    override fun apply(ops: RouteTransition.Ops, children: @Composable () -> Unit) {
        Container(
            modifier = ops[Modifier].singleOrNull() ?: androidx.ui.core.Modifier.None,
            alignment = Alignment.TopLeft,
            children = children
        )
    }
}

val DefaultRouteTransitionAmbient =
    staticAmbientOf { DefaultRouteTransition }

val DefaultRouteTransition = RouteTransition(
    definition = { defaultTransitionDefinition },
    generateOps = { _, _ -> opsOf() }
)

private val defaultTransitionDefinition = transitionDefinition {
    state(RouteTransition.State.Init) {}
    state(RouteTransition.State.EnterFromPush) {}
    state(RouteTransition.State.ExitFromPush) {}
    state(RouteTransition.State.ExitFromPop) {}
    state(RouteTransition.State.EnterFromPop) {}
}

@Composable
internal fun RouteTransitionWrapper(
    transition: RouteTransition,
    state: RouteTransition.State,
    lastState: RouteTransition.State,
    onTransitionComplete: (RouteTransition.State) -> Unit,
    types: List<RouteTransition.Type>,
    children: @Composable () -> Unit
) {
    Transition(
        definition = transition.definition(),
        toState = state,
        onStateChangeFinished = onTransitionComplete,
        initState = lastState,
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
    children: @Composable () -> Unit
) {
    types
        .map { type ->
            val function: @Composable (@Composable () -> Unit) -> Unit = { typeChildren ->
                type.apply(ops) { typeChildren() }
            }
            function
        }
        .fold(children) { current, type ->
            { type(current) }
        }.invoke()
}
