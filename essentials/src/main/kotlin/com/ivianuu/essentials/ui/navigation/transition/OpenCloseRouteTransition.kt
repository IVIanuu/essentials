package com.ivianuu.essentials.ui.navigation.transition

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import com.ivianuu.essentials.ui.navigation.CanvasRouteTransitionType
import com.ivianuu.essentials.ui.navigation.OpacityRouteTransitionType
import com.ivianuu.essentials.ui.navigation.RouteTransition
import com.ivianuu.essentials.ui.navigation.opsOf
import com.ivianuu.essentials.ui.navigation.with
import kotlin.time.Duration
import kotlin.time.milliseconds

fun OpenCloseRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
    definition = {
        remember(duration) {
            openCloseRouteTransitionDefinition(duration)
        }
    },
    generateOps = { transitionState, _ ->
        opsOf(
            OpacityRouteTransitionType.Opacity with transitionState[Alpha],
            CanvasRouteTransitionType.Block with { canvas, parentSize ->
                canvas.save()
                canvas.scale(sx = transitionState[Scale], sy = transitionState[Scale])
                canvas.translate(
                    dx = parentSize.width.value / 2 * (1f - transitionState[Scale]),
                    dy = parentSize.height.value / 2 * (1f - transitionState[Scale])
                )
                drawChildren()
                canvas.restore()
            }
        )
    }
)

private fun openCloseRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition {
    state(RouteTransition.State.Init) {
        set(Alpha, 0f)
        set(Scale, 1.125f)
    }
    state(RouteTransition.State.EnterFromPush) {
        set(Alpha, 1f)
        set(Scale, 1f)
    }
    state(RouteTransition.State.ExitFromPush) {
        set(Alpha, 0f)
        set(Scale, 0.975f)
    }
    state(RouteTransition.State.EnterFromPop) {
        set(Alpha, 1f)
        set(Scale, 1f)
    }
    state(RouteTransition.State.ExitFromPop) {
        set(Alpha, 0f)
        set(Scale, 1.075f)
    }

    transition {
        Alpha using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
        Scale using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val Alpha = FloatPropKey()
private val Scale = FloatPropKey()
