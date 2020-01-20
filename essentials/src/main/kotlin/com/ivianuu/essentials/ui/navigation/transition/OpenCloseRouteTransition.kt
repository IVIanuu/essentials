package com.ivianuu.essentials.ui.navigation.transition

import androidx.animation.FloatPropKey
import androidx.animation.LinearEasing
import androidx.animation.transitionDefinition
import androidx.compose.remember
import androidx.ui.graphics.Path
import com.ivianuu.essentials.ui.core.PathEasing
import com.ivianuu.essentials.ui.navigation.CanvasRouteTransitionType
import com.ivianuu.essentials.ui.navigation.OpacityRouteTransitionType
import com.ivianuu.essentials.ui.navigation.RouteTransition
import com.ivianuu.essentials.ui.navigation.opsOf
import com.ivianuu.essentials.ui.navigation.with
import kotlin.time.milliseconds

fun OpenCloseRouteTransition(speed: Float = 1f) = RouteTransition(
    definition = {
        remember(speed) {
            openCloseRouteTransitionDefinition(speed)
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
    speed: Float
) = transitionDefinition {
    state(RouteTransition.State.Init) {
        set(Alpha, 0f)
        set(Scale, 0.85f)
    }
    state(RouteTransition.State.EnterFromPush) {
        set(Alpha, 1f)
        set(Scale, 1f)
    }
    state(RouteTransition.State.ExitFromPush) {
        set(Alpha, 0f)
        set(Scale, 1.15f)
    }
    state(RouteTransition.State.EnterFromPop) {
        set(Alpha, 1f)
        set(Scale, 1f)
    }
    state(RouteTransition.State.ExitFromPop) {
        set(Alpha, 0f)
        set(Scale, 1.15f)
    }

    transition {
        Alpha using tween<Float> { // todo ir
            duration = (50.milliseconds * speed.toDouble()).toLongMilliseconds().toInt()
            delay = (35.milliseconds * speed.toDouble()).toLongMilliseconds().toInt() // todo 66.millis for close
            easing = LinearEasing
        }

        Scale using tween<Float> { // todo ir
            duration = (300.milliseconds * speed.toDouble()).toLongMilliseconds().toInt()
            easing = PathEasing(
                path = Path().apply {
                    moveTo(0f, 0f)
                    cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.4f)
                    cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
                }
            )
        }
    }
}

private val Alpha = FloatPropKey()
private val Scale = FloatPropKey()
