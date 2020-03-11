package com.ivianuu.essentials.android.ui.navigation.transition

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import com.ivianuu.essentials.android.ui.layout.LayoutOffset
import com.ivianuu.essentials.android.ui.navigation.ModifierRouteTransitionType
import com.ivianuu.essentials.android.ui.navigation.RouteTransition
import com.ivianuu.essentials.android.ui.navigation.opsOf
import com.ivianuu.essentials.android.ui.navigation.with
import kotlin.time.Duration
import kotlin.time.milliseconds

fun HorizontalRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
    definition = {
        remember(duration) {
            horizontalRouteTransitionDefinition(duration)
        }
    },
    generateOps = { transitionState, _ ->
        opsOf(
            ModifierRouteTransitionType.Modifier with LayoutOffset.Fraction(
                fractionX = transitionState[HorizontalOffset]
            )
        )
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
