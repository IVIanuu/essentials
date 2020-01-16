package com.ivianuu.essentials.ui.navigation.transition

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import com.ivianuu.essentials.ui.layout.LayoutPercentOffset
import com.ivianuu.essentials.ui.navigation.ModifierRouteTransitionType
import com.ivianuu.essentials.ui.navigation.RouteTransition
import com.ivianuu.essentials.ui.navigation.opsOf
import com.ivianuu.essentials.ui.navigation.with
import kotlin.time.Duration
import kotlin.time.milliseconds

fun VerticalRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
    definition = {
        remember(duration) {
            verticalRouteTransitionDefinition(duration)
        }
    },
    generateOps = { transitionState, _ ->
        opsOf(
            ModifierRouteTransitionType.Modifier with LayoutPercentOffset(
                percentY = transitionState[VerticalOffset]
            )
        )
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
        VerticalOffset using tween<Float> { // todo ir
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val VerticalOffset = FloatPropKey()