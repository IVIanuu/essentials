package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.core.drawOpacity
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import kotlin.time.Duration
import kotlin.time.milliseconds

fun FadeRouteTransition(duration: Duration = 150.milliseconds) =
    StackAnimation(
        definition = {
            remember(duration) {
                fadeRouteTransitionDefinition(duration)
            }
        },
        createModifier = { transitionState, _ ->
            Modifier.drawOpacity(transitionState[Alpha])
        }
    )

private fun fadeRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition {
    state(StackAnimation.State.Init) { set(Alpha, 0f) }
    state(StackAnimation.State.EnterFromPush) { set(Alpha, 1f) }
    state(StackAnimation.State.ExitFromPush) { set(Alpha, 0f) }
    state(StackAnimation.State.EnterFromPop) { set(Alpha, 1f) }
    state(StackAnimation.State.ExitFromPop) { set(Alpha, 0f) }

    transition {
        Alpha using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val Alpha = FloatPropKey()
