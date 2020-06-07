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
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box

@Immutable
class RouteTransition(
    val definition: @Composable () -> TransitionDefinition<State>,
    val createModifier: @Composable (TransitionState, State) -> Modifier
) {

    enum class State {
        Init,
        EnterFromPush,
        ExitFromPush,
        ExitFromPop,
        EnterFromPop
    }

}

val DefaultRouteTransitionAmbient =
    staticAmbientOf { NoOpRouteTransition }

val NoOpRouteTransition = RouteTransition(
    definition = { defaultTransitionDefinition },
    createModifier = { _, _ -> Modifier }
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
    modifier: Modifier = Modifier,
    transition: RouteTransition,
    state: RouteTransition.State,
    lastState: RouteTransition.State,
    onTransitionComplete: (RouteTransition.State) -> Unit,
    children: @Composable () -> Unit
) {
    Transition(
        definition = transition.definition(),
        toState = state,
        onStateChangeFinished = onTransitionComplete,
        initState = lastState,
        children = { transitionState ->
            Box(
                modifier = modifier + transition.createModifier(transitionState, state),
                children = children
            )
        }
    )
}
