package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import com.ivianuu.essentials.ui.layout.offsetFraction
import kotlin.time.Duration
import kotlin.time.milliseconds

fun VerticalRouteTransition(duration: Duration = 300.milliseconds) =
    StackAnimation(
        definition = {
            remember(duration) {
                verticalRouteTransitionDefinition(duration)
            }
        },
        createModifier = { transitionState, _ ->
            Modifier.offsetFraction(y = transitionState[VerticalOffset])
        }
    )

private fun verticalRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition {
    state(StackAnimation.State.Init) { set(VerticalOffset, 1f) }
    state(StackAnimation.State.EnterFromPush) { set(VerticalOffset, 0f) }
    state(StackAnimation.State.ExitFromPush) { set(VerticalOffset, 0f) }
    state(StackAnimation.State.EnterFromPop) { set(VerticalOffset, 0f) }
    state(StackAnimation.State.ExitFromPop) { set(VerticalOffset, 1f) }

    transition {
        VerticalOffset using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val VerticalOffset = FloatPropKey()
