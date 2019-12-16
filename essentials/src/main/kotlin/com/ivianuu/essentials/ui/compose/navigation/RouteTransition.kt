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
import androidx.compose.Observe
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.ui.animation.Transition
import androidx.ui.core.Draw
import androidx.ui.core.Opacity
import com.github.ajalt.timberkt.d
import kotlin.time.Duration

typealias RouteTransition = @Composable() (
    state: RouteTransitionState,
    onComplete: (RouteTransitionState) -> Unit,
    content: @Composable() () -> Unit
) -> Unit

enum class RouteTransitionState {
    Init, Pushed, Popped
}

val DefaultRouteTransition: RouteTransition = { state, onComplete, content ->
    content()
    onCommit(state) { onComplete(state) }
}

fun SimpleRouteTransition(
    definition: @Composable() () -> TransitionDefinition<RouteTransitionState>,
    render: @Composable() (transitionState: TransitionState, routeTransitionState: RouteTransitionState, content: @Composable() () -> Unit) -> Unit
): RouteTransition = { state, onComplete, content ->
    Transition(
        definition = definition(),
        toState = state,
        onStateChangeFinished = onComplete
    ) { transitionState ->
        Observe {
            Content {
                render(transitionState, state, content)
            }
        }
    }
}

@Composable
private fun Content(content: @Composable() () -> Unit) {
    content()
}

fun FadeRouteTransition(duration: Duration) = SimpleRouteTransition(
    definition = {
        remember(duration) {
            fadeRouteTransitionDefinition(duration)
        }
    },
    render = { transitionState, _, content ->
        d { "render transition ${transitionState[Alpha]}" }
        Opacity(opacity = transitionState[Alpha]) {
            content()
        }
    }
)

private fun fadeRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition<RouteTransitionState> {
    state(RouteTransitionState.Init) { set(Alpha, 0f) }
    state(RouteTransitionState.Pushed) { set(Alpha, 1f) }
    state(RouteTransitionState.Popped) { set(Alpha, 0f) }

    transition(
        RouteTransitionState.Init to RouteTransitionState.Pushed,
        RouteTransitionState.Pushed to RouteTransitionState.Popped
    ) {
        Alpha using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val Alpha = FloatPropKey()

fun VerticalRouteTransition(duration: Duration) = SimpleRouteTransition(
    definition = {
        remember(duration) {
            verticalRouteTransitionDefinition(duration)
        }
    },
    render = { transitionState, _, content ->
        Draw(
            children = content,
            onPaint = { canvas, parentSize ->
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
) = transitionDefinition<RouteTransitionState> {
    state(RouteTransitionState.Init) { set(Progress, 0f) }
    state(RouteTransitionState.Pushed) { set(Progress, 1f) }
    state(RouteTransitionState.Popped) { set(Progress, 0f) }

    transition(
        RouteTransitionState.Init to RouteTransitionState.Pushed,
        RouteTransitionState.Pushed to RouteTransitionState.Popped
    ) {
        Progress using tween {
            this.duration = duration.toLongMilliseconds().toInt()
            d { "durtation = ${this.duration}" }
        }
    }
}

private val Progress = FloatPropKey()