package com.ivianuu.essentials.ui.compose.material

import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Draw
import androidx.ui.core.PxPosition
import androidx.ui.core.WithConstraints
import androidx.ui.core.ambientDensity
import androidx.ui.core.dp
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.gesture.RawDragGestureDetector
import androidx.ui.core.withDensity
import androidx.ui.engine.geometry.Offset
import androidx.ui.engine.geometry.Rect
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.layout.Container
import androidx.ui.layout.Stack
import androidx.ui.material.ripple.CurrentRippleTheme
import androidx.ui.material.themeColor
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun Slider(
    value: Int,
    onChanged: (Int) -> Unit,
    onChangeStart: ((Int) -> Unit)? = null,
    onChangeEnd: ((Int) -> Unit)? = null,
    min: Int = 0,
    max: Int = 100,
    divisions: Int = 1,
    color: Color = +themeColor { secondary }
) = composable("Slider") {
    WithConstraints { constraints ->
        val (internalValue, setInternalValue) = +state {
            lerp(unlerp(value.toFloat(), min, max), 0, constraints.maxWidth.value)
        }
        val rippleAnim = +animatedFloat(0f)

        fun notifyChangeStart() {
            onChangeStart?.invoke(
                lerp(
                    unlerp(internalValue, 0, constraints.maxWidth.value),
                    min,
                    max
                ).toInt().coerceIn(min, max)
            )
        }

        val notifiedChangeValue = +memo { NotifiedValue(value) }

        fun notifyChange() {
            val newValue = lerp(
                unlerp(internalValue, 0, constraints.maxWidth.value),
                min,
                max
            ).toInt().coerceIn(min, max)
            if (notifiedChangeValue.value != newValue) {
                notifiedChangeValue.value = newValue
                onChanged.invoke(newValue)
            }
        }

        fun notifyChangeEnd() {
            onChangeEnd?.invoke(
                lerp(
                    unlerp(internalValue, 0, constraints.maxWidth.value),
                    min,
                    max
                ).toInt().coerceIn(min, max)
            )
        }

        notifyChange()

        RawDragGestureDetector(dragObserver = object : DragObserver {
            override fun onStart(downPosition: PxPosition) {
                super.onStart(downPosition)
                rippleAnim.animateTo(
                    targetValue = 1f,
                    anim = TweenBuilder<Float>().apply { duration = 250 }
                )
                notifyChangeStart()
            }

            override fun onDrag(dragDistance: PxPosition): PxPosition {
                setInternalValue(internalValue + dragDistance.x.value)
                notifyChange()
                return dragDistance
            }

            override fun onStop(velocity: PxPosition) {
                super.onStop(velocity)
                rippleAnim.animateTo(
                    targetValue = 0f,
                    anim = TweenBuilder<Float>().apply { duration = 250 }
                )
                notifyChangeEnd()
            }
        }) {
            PressGestureDetector(
                onPress = { position ->
                    notifyChangeStart()
                    setInternalValue(position.x.value)
                    notifyChange()
                    notifyChangeEnd()
                }
            ) {
                Container(height = 60.dp, expanded = true) {
                    Stack {
                        DrawSlider(internalValue, rippleAnim.value, color)
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawSlider(
    x: Float,
    rippleValue: Float,
    color: Color
) = composable("DrawSlider") {
    val paint = +memo { Paint() }
    val rippleColor = (+(+ambient(CurrentRippleTheme)).defaultColor).copy(alpha = 0.12f)
    val barHeight = withDensity(+ambientDensity()) { BarHeight.toPx() }.value
    val sliderRadius = withDensity(+ambientDensity()) { SliderRadius.toPx() }.value

    Draw { canvas, parentSize ->
        val centerY = parentSize.height.value / 2
        val constraintX = x.coerceIn(0f, parentSize.width.value)

        // bar
        paint.color = color.copy(alpha = 0.12f)
        canvas.drawRect(
            Rect(0f, centerY - barHeight / 2, parentSize.width.value, centerY + barHeight),
            paint
        )

        // progress bar
        paint.color = color
        canvas.drawRect(
            Rect(0f, centerY - barHeight, constraintX, centerY + barHeight),
            paint
        )

        // ripple
        paint.color = rippleColor.copy(alpha = 0.12f * rippleValue)
        canvas.drawCircle(
            Offset(constraintX, centerY), sliderRadius * 2.5f * rippleValue, paint
        )

        // indicator
        paint.color = color
        canvas.drawCircle(
            Offset(constraintX, centerY), sliderRadius, paint
        )
    }
}

private val BarHeight = 1.5.dp
private val SliderRadius = 6.dp

private data class NotifiedValue(var value: Int)

private fun unlerp(
    value: Float,
    min: Int,
    max: Int
): Float = (value - min) / (max - min)

private fun lerp(
    value: Float,
    min: Int,
    max: Int
): Float = (value * (max - min) + min)

