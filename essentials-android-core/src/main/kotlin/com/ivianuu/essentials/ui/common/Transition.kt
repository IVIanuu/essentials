package com.ivianuu.essentials.ui.common

import androidx.animation.AnimationClockObservable
import androidx.animation.AnimationVector
import androidx.animation.PropKey
import androidx.animation.TransitionAnimation
import androidx.animation.TransitionDefinition
import androidx.animation.TransitionState
import androidx.animation.createAnimation
import androidx.compose.Composable
import androidx.compose.Stable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.onPreCommit
import androidx.compose.remember
import androidx.compose.setValue
import androidx.ui.core.AnimationClockAmbient

@Composable
fun <T> transition(
    definition: TransitionDefinition<T>,
    toState: T,
    clock: AnimationClockObservable = AnimationClockAmbient.current,
    initState: T = toState,
    onStateChangeFinished: ((T) -> Unit)? = null
): TransitionState {
    val model = remember(definition, clock) {
        TransitionModel(definition, initState, clock)
    }
    model.anim.onStateChangeFinished = onStateChangeFinished
    onPreCommit(model, toState) {
        model.anim.toState(toState)
    }

    return model
}

@Stable
private class TransitionModel<T>(
    transitionDef: TransitionDefinition<T>,
    initState: T,
    clock: AnimationClockObservable
) : TransitionState {

    private var animationPulse by mutableStateOf(0L)
    internal val anim: TransitionAnimation<T> =
        transitionDef.createAnimation(clock, initState).apply {
            onUpdate = {
                animationPulse++
            }
        }

    override fun <T, V : AnimationVector> get(propKey: PropKey<T, V>): T {
        // we need to access the animationPulse so Compose will record this state values usage.
        @Suppress("UNUSED_VARIABLE")
        val pulse = animationPulse
        return anim[propKey]
    }
}
