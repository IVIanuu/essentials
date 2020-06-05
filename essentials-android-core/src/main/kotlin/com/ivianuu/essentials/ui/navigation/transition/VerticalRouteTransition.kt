package com.ivianuu.essentials.ui.navigation.transition

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.layout.offsetFraction
import com.ivianuu.essentials.ui.navigation.RouteTransition
import kotlin.time.Duration
import kotlin.time.milliseconds

fun VerticalRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
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
    state(RouteTransition.State.Init) { set(VerticalOffset, 1f) }
    state(RouteTransition.State.EnterFromPush) { set(VerticalOffset, 0f) }
    state(RouteTransition.State.ExitFromPush) { set(VerticalOffset, 0f) }
    state(RouteTransition.State.EnterFromPop) { set(VerticalOffset, 0f) }
    state(RouteTransition.State.ExitFromPop) { set(VerticalOffset, 1f) }

    transition {
        VerticalOffset using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val VerticalOffset = FloatPropKey()
