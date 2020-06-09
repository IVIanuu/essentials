package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import com.ivianuu.essentials.ui.layout.offsetFraction
import kotlin.time.Duration
import kotlin.time.milliseconds

fun HorizontalRouteTransition(duration: Duration = 300.milliseconds) =
    StackAnimation(
        definition = {
            remember(duration) {
                horizontalRouteTransitionDefinition(duration)
            }
        },
        createModifier = { transitionState, _ ->
            Modifier.offsetFraction(x = transitionState[HorizontalOffset])
        }
    )

private fun horizontalRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition {
    state(StackAnimation.State.Init) { set(HorizontalOffset, 1f) }
    state(StackAnimation.State.EnterFromPush) { set(HorizontalOffset, 0f) }
    state(StackAnimation.State.ExitFromPush) { set(HorizontalOffset, -1f) }
    state(StackAnimation.State.EnterFromPop) { set(HorizontalOffset, 0f) }
    state(StackAnimation.State.ExitFromPop) { set(HorizontalOffset, 1f) }

    transition {
        HorizontalOffset using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val HorizontalOffset = FloatPropKey()
