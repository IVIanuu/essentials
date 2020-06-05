package com.ivianuu.essentials.ui.navigation.transition

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.core.drawOpacity
import com.ivianuu.essentials.ui.navigation.RouteTransition
import kotlin.time.Duration
import kotlin.time.milliseconds

fun FadeRouteTransition(duration: Duration = 150.milliseconds) = RouteTransition(
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
    state(RouteTransition.State.Init) { set(Alpha, 0f) }
    state(RouteTransition.State.EnterFromPush) { set(Alpha, 1f) }
    state(RouteTransition.State.ExitFromPush) { set(Alpha, 0f) }
    state(RouteTransition.State.EnterFromPop) { set(Alpha, 1f) }
    state(RouteTransition.State.ExitFromPop) { set(Alpha, 0f) }

    transition {
        Alpha using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val Alpha = FloatPropKey()
