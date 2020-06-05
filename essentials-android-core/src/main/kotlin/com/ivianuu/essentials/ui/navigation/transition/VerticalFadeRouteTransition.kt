package com.ivianuu.essentials.ui.navigation.transition

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.core.drawOpacity
import com.ivianuu.essentials.ui.layout.offsetFraction
import com.ivianuu.essentials.ui.navigation.RouteTransition
import kotlin.time.Duration
import kotlin.time.milliseconds

fun VerticalFadeRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
    definition = {
        remember(duration) {
            verticalFadeRouteTransitionDefinition(duration)
        }
    },
    createModifier = { transitionState, _ ->
        Modifier
            .offsetFraction(y = transitionState[VerticalOffset])
            .drawOpacity(transitionState[Alpha])
    }
)

private fun verticalFadeRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition {
    state(RouteTransition.State.Init) {
        set(Alpha, 0f)
        set(VerticalOffset, 0.3f)
    }
    state(RouteTransition.State.EnterFromPush) {
        set(Alpha, 1f)
        set(VerticalOffset, 0f)
    }
    state(RouteTransition.State.ExitFromPush) {
        set(Alpha, 1f)
        set(VerticalOffset, 0f)
    }
    state(RouteTransition.State.EnterFromPop) {
        set(Alpha, 1f)
        set(VerticalOffset, 0f)
    }
    state(RouteTransition.State.ExitFromPop) {
        set(Alpha, 0f)
        set(VerticalOffset, 0.3f)
    }

    transition {
        Alpha using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
        VerticalOffset using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val Alpha = FloatPropKey()
private val VerticalOffset = FloatPropKey()
