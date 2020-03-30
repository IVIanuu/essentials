package com.ivianuu.essentials.ui.navigation.transition

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.core.TransformOrigin
import androidx.ui.core.drawLayer
import com.ivianuu.essentials.ui.navigation.ModifierRouteTransitionType
import com.ivianuu.essentials.ui.navigation.RouteTransition
import com.ivianuu.essentials.ui.navigation.opsOf
import com.ivianuu.essentials.ui.navigation.with
import kotlin.time.Duration
import kotlin.time.milliseconds

fun ScaleRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
    definition = {
        remember(duration) {
            scaleRouteTransitionDefinition(duration)
        }
    },
    generateOps = { transitionState, _ ->
        opsOf(
            ModifierRouteTransitionType.Modifier with Modifier.drawLayer(
                scaleX = transitionState[Scale], scaleY = transitionState[Scale],
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            )
        )
    }
)

private fun scaleRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition {
    state(RouteTransition.State.Init) { set(Scale, 0f) }
    state(RouteTransition.State.EnterFromPush) { set(Scale, 1f) }
    state(RouteTransition.State.ExitFromPush) { set(Scale, 1f) }
    state(RouteTransition.State.EnterFromPop) { set(Scale, 1f) }
    state(RouteTransition.State.ExitFromPop) { set(Scale, 0f) }

    transition {
        Scale using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val Scale = FloatPropKey()
