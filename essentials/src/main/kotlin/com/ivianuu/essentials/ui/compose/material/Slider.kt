package com.ivianuu.essentials.ui.compose.material

import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Draw
import androidx.ui.core.Opacity
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
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun Slider(
    value: Int,
    onChanged: ((Int) -> Unit)?,
    onChangeStart: ((Int) -> Unit)? = null,
    onChangeEnd: ((Int) -> Unit)? = null,
    min: Int = 0,
    max: Int = 100,
    divisions: Int? = null,
    color: Color = +themeColor { secondary }
) = composable("Slider") {
    WithConstraints { constraints ->
        val (internalValue, setInternalValue) = +state {
            lerp(unlerp(value.toFloat(), min, max), 0, constraints.maxWidth.value).toInt()
        }
        val rippleAnim = +animatedFloat(0f)

        fun notifyChangeStart() {
            val unlerped1 = unlerp(internalValue.toFloat(), 0, constraints.maxWidth.value)
            val lerped = lerp(unlerped1, min, max).toInt()
            val sliderValue = discretize(lerped, divisions?.let { max / it })
            onChangeStart?.invoke(sliderValue)
        }

        val notifiedChangeValue = +memo { NotifiedValue(value) }

        fun notifyChange() {
            val unlerped1 = unlerp(internalValue.toFloat(), 0, constraints.maxWidth.value)
            val lerped = lerp(unlerped1, min, max).toInt()
            val sliderValue = discretize(lerped, divisions?.let { max / it })
            if (notifiedChangeValue.value != sliderValue) {
                notifiedChangeValue.value = sliderValue
                onChanged?.invoke(sliderValue)
            }
        }

        fun notifyChangeEnd() {
            val unlerped1 = unlerp(internalValue.toFloat(), 0, constraints.maxWidth.value)
            val lerped = lerp(unlerped1, min, max).toInt()
            val sliderValue = discretize(lerped, divisions?.let { max / it })
            onChangeEnd?.invoke(sliderValue)
        }

        notifyChange()

        RawDragGestureDetector(dragObserver = object : DragObserver {
            override fun onStart(downPosition: PxPosition) {
                super.onStart(downPosition)
                if (onChanged != null) {
                    rippleAnim.animateTo(
                        targetValue = 1f,
                        anim = TweenBuilder<Float>().apply { duration = 250 }
                    )
                    notifyChangeStart()
                }
            }

            override fun onDrag(dragDistance: PxPosition): PxPosition {
                if (onChanged != null) {
                    setInternalValue((internalValue + dragDistance.x.value).toInt())
                    notifyChange()
                }
                return dragDistance
            }

            override fun onStop(velocity: PxPosition) {
                super.onStop(velocity)
                if (onChanged != null) {
                    rippleAnim.animateTo(
                        targetValue = 0f,
                        anim = TweenBuilder<Float>().apply { duration = 250 }
                    )
                    notifyChangeEnd()
                }
            }
        }) {
            PressGestureDetector(
                onPress = { position ->
                    if (onChanged != null) {
                        notifyChangeStart()
                        setInternalValue(position.x.value.toInt())
                        notifyChange()
                        notifyChangeEnd()
                    }
                }
            ) {
                Container(height = 60.dp, expanded = true) {
                    Stack {
                        val discrete = discretize(
                            internalValue,
                            divisions?.let { constraints.maxWidth.value / it }).toFloat()
                        DrawSlider(
                            discrete,
                            rippleAnim.value,
                            color,
                            divisions,
                            onChanged != null
                        )
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
    color: Color,
    divisions: Int?,
    enabled: Boolean
) = composable("DrawSlider") {
    val paint = +memo { Paint() }
    val rippleColor = (+(+ambient(CurrentRippleTheme)).defaultColor).copy(alpha = 0.12f)
    val barHeight = withDensity(+ambientDensity()) { BarHeight.toPx() }.value
    var sliderRadius = withDensity(+ambientDensity()) { SliderRadius.toPx() }.value
    if (!enabled) {
        sliderRadius *= 0.75f
    }
    val sliderMargin = sliderRadius * 1.5f
    val surfaceColor = +themeColor { surface }

    Opacity(opacity = if (enabled) 1f else 0.5f) {
        Draw { canvas, parentSize ->
            val centerY = parentSize.height.value / 2
            val constraintX = x.coerceIn(0f, parentSize.width.value)

            // bar 1
            paint.color = color.copy(alpha = 0.12f)
            canvas.drawRect(
                Rect(0f, centerY - barHeight / 2, constraintX - sliderMargin, centerY + barHeight),
                paint
            )

            // progress bar
            paint.color = color
            canvas.drawRect(
                Rect(0f, centerY - barHeight / 2, constraintX - sliderMargin, centerY + barHeight),
                paint
            )

            // bar 2
            paint.color = color.copy(alpha = 0.12f)
            canvas.drawRect(
                Rect(
                    constraintX + sliderMargin,
                    centerY - barHeight / 2,
                    parentSize.width.value,
                    centerY + barHeight
                ),
                paint
            )

            // divisions
            if (divisions != null) {
                (0..divisions).forEach { division ->
                    val step = parentSize.width.value / divisions
                    val divisionX = (step * division)
                    if (divisionX < x) {
                        paint.color = surfaceColor.copy(alpha = 0.36f)
                    } else {
                        paint.color = color.copy(alpha = 0.24f)
                    }
                    canvas.drawCircle(
                        Offset(divisionX, centerY),
                        barHeight,
                        paint
                    )
                }
            }

            // ripple
            if (rippleValue > 0f) {
                paint.color = rippleColor.copy(alpha = rippleColor.alpha * rippleValue)
                canvas.drawCircle(
                    Offset(constraintX, centerY), sliderRadius * 2.5f * rippleValue, paint
                )
            }

            // indicator
            paint.color = color
            canvas.drawCircle(
                Offset(constraintX, centerY), sliderRadius, paint
            )
        }
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

private fun discretize(
    value: Int,
    decimals: Int?
): Int {
    if (decimals == null) return value
    val a = value / decimals * decimals
    val b = a + decimals
    val result = if (value - a > b - value) b else a
    d { "discretize: input $value, deci $decimals, a $a, b $b, result $result" }
    return result
}