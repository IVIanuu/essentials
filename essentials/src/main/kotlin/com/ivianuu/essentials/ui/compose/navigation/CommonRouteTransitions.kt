/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.compose.navigation

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.remember
import kotlin.time.Duration
import kotlin.time.milliseconds

val FadeRouteTransitionKey = RouteTransition.Key()

fun FadeRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
    key = FadeRouteTransitionKey,
    definition = {
        remember(duration) {
            fadeRouteTransitionDefinition(duration)
        }
    },
    generateOps = { transitionState, _ ->
        opsOf(
            OpacityRouteTransitionType.Opacity with transitionState[Alpha]
        )
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

val VerticalRouteTransitionKey = RouteTransition.Key()
fun VerticalRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
    key = VerticalRouteTransitionKey,
    definition = {
        remember(duration) {
            verticalRouteTransitionDefinition(duration)
        }
    },
    generateOps = { transitionState, _ ->
        opsOf(
            CanvasRouteTransitionType.Block with { canvas, parentSize ->
                canvas.save()
                canvas.translate(0f, parentSize.height.value * (1f - transitionState[Fraction]))
                drawChildren()
                canvas.restore()
            }
        )
    }
)

private fun verticalRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition {
    state(RouteTransition.State.Init) { set(Fraction, 0f) }
    state(RouteTransition.State.EnterFromPush) { set(Fraction, 1f) }
    state(RouteTransition.State.ExitFromPush) { set(Fraction, 1f) }
    state(RouteTransition.State.EnterFromPop) { set(Fraction, 1f) }
    state(RouteTransition.State.ExitFromPop) { set(Fraction, 0f) }

    transition {
        Fraction using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

private val Fraction = FloatPropKey()

val HorizontalRouteTransitionKey = RouteTransition.Key()
fun HorizontalRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
    key = HorizontalRouteTransitionKey,
    definition = {
        remember(duration) {
            horizontalRouteTransitionDefinition(duration)
        }
    },
    generateOps = { transitionState, _ ->
        opsOf(
            CanvasRouteTransitionType.Block with { canvas, parentSize ->
                canvas.save()
                canvas.translate(parentSize.width.value * transitionState[Fraction], 0f)
                drawChildren()
                canvas.restore()
            }
        )
    }
)

private fun horizontalRouteTransitionDefinition(
    duration: Duration
) = transitionDefinition {
    state(RouteTransition.State.Init) { set(Fraction, 1f) }
    state(RouteTransition.State.EnterFromPush) { set(Fraction, 0f) }
    state(RouteTransition.State.ExitFromPush) { set(Fraction, -1f) }
    state(RouteTransition.State.EnterFromPop) { set(Fraction, 0f) }
    state(RouteTransition.State.ExitFromPop) { set(Fraction, 1f) }

    transition {
        Fraction using tween {
            this.duration = duration.toLongMilliseconds().toInt()
        }
    }
}

val OpenCloseRouteTransitionKey = RouteTransition.Key()
fun OpenCloseRouteTransition(duration: Duration = 300.milliseconds) = RouteTransition(
    key = OpenCloseRouteTransitionKey,
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

private val Scale = FloatPropKey()
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
