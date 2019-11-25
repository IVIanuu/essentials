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

package com.ivianuu.essentials.ui.compose.material

import androidx.animation.FloatPropKey
import androidx.animation.TransitionSpec
import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.ui.animation.ColorPropKey
import androidx.ui.animation.Transition
import androidx.ui.core.Draw
import androidx.ui.core.dp
import androidx.ui.engine.geometry.Offset
import androidx.ui.engine.geometry.RRect
import androidx.ui.engine.geometry.Radius
import androidx.ui.engine.geometry.shrink
import androidx.ui.engine.geometry.withRadius
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.PaintingStyle
import androidx.ui.graphics.StrokeCap
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.layout.Wrap
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.core.memo

// todo remove once original is useable

@Composable
fun EsCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    color: Color = MaterialTheme.colors()().secondary
) = composable {
    Wrap {
        Ripple(bounded = false) {
            Clickable(onClick = onCheckedChange?.let { { onCheckedChange(!checked) } }) {
                Padding(padding = CheckboxDefaultPadding) {
                    Container(width = CheckboxSize, height = CheckboxSize) {
                        DrawCheckbox(checked = checked, activeColor = color)
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawCheckbox(checked: Boolean, activeColor: Color) = composable {
    val unselectedColor = MaterialTheme.colors()().onSurface.copy(alpha = UncheckedBoxOpacity)
    val definition = memo(activeColor, unselectedColor) {
        generateTransitionDefinition(activeColor, unselectedColor)
    }
    Transition(definition = definition, toState = checked) { state ->
        DrawBox(
            color = state[BoxColorProp],
            innerRadiusFraction = state[InnerRadiusFractionProp]
        )
        DrawCheck(
            checkFraction = state[CheckFractionProp],
            crossCenterGravitation = state[CenterGravitationForCheck]
        )
    }
}

@Composable
private fun DrawBox(color: Color, innerRadiusFraction: Float) = composable {
    Draw { canvas, parentSize ->
        val paint = Paint()
        paint.strokeWidth = StrokeWidth.toPx().value
        paint.isAntiAlias = true
        paint.color = color

        val checkboxSize = parentSize.width.value

        val outer = RRect(
            0f,
            0f,
            checkboxSize,
            checkboxSize,
            Radius.circular(RadiusSize.toPx().value)
        )

        val shrinkTo = calcMiddleValue(
            paint.strokeWidth,
            outer.width / 2,
            innerRadiusFraction
        )
        val innerSquared = outer.shrink(shrinkTo)
        val squareMultiplier = innerRadiusFraction * innerRadiusFraction

        // TODO(malkov): this radius formula is not in material spec
        val inner = innerSquared
            .withRadius(Radius.circular(innerSquared.width * squareMultiplier))
        canvas.drawDoubleRoundRect(outer, inner, paint)
    }
}

@Composable
private fun DrawCheck(
    checkFraction: Float,
    crossCenterGravitation: Float
) = composable {
    Draw { canvas, parentSize ->
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = PaintingStyle.stroke
        paint.strokeCap = StrokeCap.square
        paint.strokeWidth = StrokeWidth.toPx().value
        paint.color = CheckStrokeDefaultColor

        val width = parentSize.width.value

        val checkCrossX = 0.4f
        val checkCrossY = 0.7f
        val leftX = 0.2f
        val leftY = 0.5f
        val rightX = 0.8f
        val rightY = 0.3f

        val gravitatedCrossX = calcMiddleValue(checkCrossX, 0.5f, crossCenterGravitation)
        val gravitatedCrossY = calcMiddleValue(checkCrossY, 0.5f, crossCenterGravitation)

        // gravitate only Y for end to achieve center line
        val gravitatedLeftY = calcMiddleValue(leftY, 0.5f, crossCenterGravitation)
        val gravitatedRightY = calcMiddleValue(rightY, 0.5f, crossCenterGravitation)

        val crossPoint = Offset(width * gravitatedCrossX, width * gravitatedCrossY)
        val rightBranch = Offset(
            width * calcMiddleValue(gravitatedCrossX, rightX, checkFraction),
            width * calcMiddleValue(gravitatedCrossY, gravitatedRightY, checkFraction)
        )
        val leftBranch = Offset(
            width * calcMiddleValue(gravitatedCrossX, leftX, checkFraction),
            width * calcMiddleValue(gravitatedCrossY, gravitatedLeftY, checkFraction)
        )
        if (checkFraction > 0f) {
            canvas.drawLine(crossPoint, leftBranch, paint)
            canvas.drawLine(crossPoint, rightBranch, paint)
        }
    }
}

private fun calcMiddleValue(start: Float, finish: Float, fraction: Float): Float =
    start * (1 - fraction) + finish * fraction

// all float props are fraction now [0f .. 1f] as it seems convenient
private val InnerRadiusFractionProp = FloatPropKey()
private val CheckFractionProp = FloatPropKey()
private val CenterGravitationForCheck = FloatPropKey()
private val BoxColorProp = ColorPropKey()

private const val BoxAnimationDuration = 100
private const val CheckStrokeAnimationDuration = 100

private fun generateTransitionDefinition(color: Color, unselectedColor: Color) =
    transitionDefinition<Boolean> {
        state(false) {
            this[CheckFractionProp] = 0f
            this[InnerRadiusFractionProp] = 0f
            this[CenterGravitationForCheck] = 1f
            this[BoxColorProp] = unselectedColor
        }
        state(true) {
            this[CheckFractionProp] = 1f
            this[InnerRadiusFractionProp] = 1f
            this[CenterGravitationForCheck] = 0f
            this[BoxColorProp] = color
        }
        transition(fromState = false, toState = true) {
            boxTransitionFromUnchecked()
            CenterGravitationForCheck using snap()
        }
        transition(fromState = true, toState = false) {
            boxTransitionToUnchecked()
            CenterGravitationForCheck using tween {
                duration = CheckStrokeAnimationDuration
            }
        }
    }

private fun TransitionSpec<Boolean>.boxTransitionFromUnchecked() {
    BoxColorProp using snap()
    InnerRadiusFractionProp using tween {
        duration = BoxAnimationDuration
    }
    CheckFractionProp using tween {
        duration = CheckStrokeAnimationDuration
        delay = BoxAnimationDuration
    }
}

private fun TransitionSpec<Boolean>.boxTransitionToUnchecked() {
    BoxColorProp using snap()
    InnerRadiusFractionProp using tween {
        duration = BoxAnimationDuration
        delay = CheckStrokeAnimationDuration
    }
    CheckFractionProp using tween {
        duration = CheckStrokeAnimationDuration
    }
}

private val CheckboxDefaultPadding = 2.dp
private val CheckboxSize = 20.dp
private val StrokeWidth = 2.dp
private val RadiusSize = 2.dp

private val UncheckedBoxOpacity = 0.6f
private val CheckStrokeDefaultColor = Color.White