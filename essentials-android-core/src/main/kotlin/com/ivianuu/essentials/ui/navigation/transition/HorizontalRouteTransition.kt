package com.ivianuu.essentials.ui.navigation.transition

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.layout.offsetFraction
import com.ivianuu.essentials.ui.navigation.RouteTransition
import kotlin.time.Duration
import kotlin.time.milliseconds

fun HorizontalRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
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
    state(RouteTransition.State.Init) { set(HorizontalOffset, 1f) }
    state(RouteTransition.State.EnterFromPush) { set(HorizontalOffset, 0f) }
    state(RouteTransition.State.ExitFromPush) { set(HorizontalOffset, -1f) }
    state(RouteTransition.State.EnterFromPop) { set(HorizontalOffset, 0f) }
    state(RouteTransition.State.ExitFromPop) { set(HorizontalOffset, 1f) }

    transition {
        HorizontalOffset using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val HorizontalOffset = FloatPropKey()
