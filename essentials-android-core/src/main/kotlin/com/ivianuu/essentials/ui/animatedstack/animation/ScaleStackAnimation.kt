package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.core.TransformOrigin
import androidx.ui.core.drawLayer
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import kotlin.time.Duration
import kotlin.time.milliseconds

fun ScaleRouteTransition(duration: Duration = 300.milliseconds) =
    StackAnimation(
        definition = {
            remember(duration) {
                scaleRouteTransitionDefinition(duration)
            }
        },
        createModifier = { transitionState, _ ->
            Modifier.drawLayer(
                scaleX = transitionState[Scale], scaleY = transitionState[Scale],
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            )
        }
    )

private fun scaleRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition {
    state(StackAnimation.State.Init) { set(Scale, 0f) }
    state(StackAnimation.State.EnterFromPush) { set(Scale, 1f) }
    state(StackAnimation.State.ExitFromPush) { set(Scale, 1f) }
    state(StackAnimation.State.EnterFromPop) { set(Scale, 1f) }
    state(StackAnimation.State.ExitFromPop) { set(Scale, 0f) }

    transition {
        Scale using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val Scale = FloatPropKey()
