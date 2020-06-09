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

package com.ivianuu.essentials.ui.animatedstack

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
class StackAnimation(
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

val DefaultStackAnimationAmbient =
    staticAmbientOf { NoOpStackAnimation }

val NoOpStackAnimation =
    StackAnimation(
        definition = { defaultStackAnimationDefinition },
        createModifier = { _, _ -> Modifier }
    )

private val defaultStackAnimationDefinition = transitionDefinition {
    state(StackAnimation.State.Init) {}
    state(StackAnimation.State.EnterFromPush) {}
    state(StackAnimation.State.ExitFromPush) {}
    state(StackAnimation.State.ExitFromPop) {}
    state(StackAnimation.State.EnterFromPop) {}
}

@Composable
internal fun StackAnimation(
    modifier: Modifier = Modifier,
    animation: StackAnimation,
    state: StackAnimation.State,
    lastState: StackAnimation.State,
    onComplete: (StackAnimation.State) -> Unit,
    children: @Composable () -> Unit
) {
    Transition(
        definition = animation.definition(),
        toState = state,
        onStateChangeFinished = onComplete,
        initState = lastState,
        children = { transitionState ->
            Box(
                modifier = modifier + animation.createModifier(transitionState, state),
                children = children
            )
        }
    )
}
