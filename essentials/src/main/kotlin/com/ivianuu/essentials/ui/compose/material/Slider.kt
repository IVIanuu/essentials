package com.ivianuu.essentials.ui.compose.material

import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Draw
import androidx.ui.core.Opacity
import androidx.ui.core.PxPosition
import androidx.ui.core.WithConstraints
import androidx.ui.core.dp
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.gesture.RawDragGestureDetector
import androidx.ui.engine.geometry.Offset
import androidx.ui.engine.geometry.Rect
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.layout.Container
import androidx.ui.layout.Stack
import androidx.ui.lerp
import androidx.ui.material.themeColor
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.ref

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
            val fraction = unlerp(value.toFloat(), max.toFloat(), min.toFloat())
            lerp(0f, constraints.maxWidth.value.toFloat(), fraction)
        }

        val overlayAnim = +animatedFloat(0f)

        fun getCurrentFraction(): Float =
            unlerp(internalValue, constraints.maxWidth.value.toFloat(), 0f)
                .coerceIn(0f, 1f)

        fun getCurrentUserValue(): Int {
            return lerp(min, max, getCurrentFraction())
                .coerceIn(min, max)
                .let { value ->
                    discretize(value, divisions?.let { divisions ->
                        max / divisions
                    })
                }
        }

        fun notifyChangeStart() {
            onChangeStart?.invoke(getCurrentUserValue())
        }

        val notifiedChangeValue = +ref { value }
        fun notifyChange() {
            val currentValue = getCurrentUserValue()
            if (notifiedChangeValue.value != currentValue) {
                notifiedChangeValue.value = currentValue
                onChanged?.invoke(currentValue)
            }
        }

        fun notifyChangeEnd() {
            onChangeEnd?.invoke(getCurrentUserValue())
        }

        notifyChange()

        RawDragGestureDetector(dragObserver = object : DragObserver {
            override fun onStart(downPosition: PxPosition) {
                super.onStart(downPosition)
                if (onChanged != null) {
                    overlayAnim.animateTo(
                        targetValue = 1f,
                        anim = TweenBuilder<Float>().apply { duration = 250 }
                    )

                    notifyChangeStart()
                }
            }

            override fun onDrag(dragDistance: PxPosition): PxPosition {
                if (onChanged != null) {
                    setInternalValue(internalValue + dragDistance.x.value)
                    notifyChange()
                }
                return dragDistance
            }

            override fun onStop(velocity: PxPosition) {
                super.onStop(velocity)
                if (onChanged != null) {
                    overlayAnim.animateTo(
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
                        setInternalValue(position.x.value)
                        notifyChange()
                        notifyChangeEnd()
                    }
                }
            ) {
                Container(height = ContainerHeight, expanded = true) {
                    //ColoredRect(Color.Red.copy(alpha = 0.1f))
                    Stack {
                        DrawSlider(
                            getCurrentFraction(),
                            overlayAnim.value,
                            color,
                            +themeColor { surface },
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
    fraction: Float,
    overlayFraction: Float,
    color: Color,
    surfaceColor: Color,
    divisions: Int?,
    enabled: Boolean
) = composable("DrawSlider") {
    val paint = +memo { Paint() }

    Opacity(opacity = if (enabled) 1f else 0.5f) {
        Draw { canvas, parentSize ->
            val thumbOverlayRadius = OverlayRadius.toPx().value

            val centerY = parentSize.height.value / 2
            val startX = thumbOverlayRadius
            val endX = parentSize.width.value - thumbOverlayRadius
            val currentX = lerp(0f, endX, fraction)
                .let { tmp ->
                    if (divisions != null) {
                        discretize(tmp.toInt(), endX.toInt() / divisions).toFloat()
                            .coerceIn(startX, endX)
                    } else {
                        tmp
                    }
                }


            val trackHeight = TrackHeight.toPx().value

            // track
            paint.color =
                color.copy(alpha = if (enabled) InactiveTrackAlpha else DisabledInactiveTrackAlpha)
            canvas.drawRect(
                Rect(startX, centerY - trackHeight / 2, endX, centerY + trackHeight / 2),
                paint
            )

            // active track
            paint.color =
                color.copy(alpha = if (enabled) ActiveTrackAlpha else DisabledActiveTrackAlpha)
            canvas.drawRect(
                Rect(startX, centerY - trackHeight / 2, currentX, centerY + trackHeight / 2),
                paint
            )

            // tick marks
            if (divisions != null) {
                (0..divisions).forEach { division ->
                    val step = endX / divisions
                    val tickMarkX = (step * division)
                    if (tickMarkX <= currentX) {
                        paint.color = surfaceColor.copy(
                            alpha = if (enabled) ActiveTickMarkAlpha else DisabledActiveTickMarkAlpha
                        )
                    } else {
                        paint.color = color.copy(
                            alpha = if (enabled) InactiveTickMarkAlpha else DisabledInactiveTickMarkAlpha
                        )
                    }
                    canvas.drawCircle(
                        Offset(tickMarkX, centerY),
                        trackHeight / 2,
                        paint
                    )
                }
            }

            // overlay
            if (overlayFraction > 0f) {
                paint.color = color.copy(alpha = OverlayAlpha * overlayFraction)
                canvas.drawCircle(
                    Offset(currentX, centerY), thumbOverlayRadius * overlayFraction, paint
                )
            }

            // disabled outline
            if (!enabled) {
                paint.color = surfaceColor
                canvas.drawCircle(Offset(currentX, centerY), ThumbOutlineRadius.toPx().value, paint)
            }

            // thumb
            val thumbRadius =
                if (enabled) ThumbRadius.toPx().value else DisabledThumbRadius.toPx().value
            paint.color = color.copy(alpha = if (enabled) ThumbAlpha else DisabledThumbAlpha)
            canvas.drawCircle(Offset(currentX, centerY), thumbRadius, paint)
        }
    }
}

private val ContainerHeight = 32.dp
private val TrackHeight = 2.dp
private val ThumbRadius = 6.dp
private val DisabledThumbRadius = 4.dp
private val OverlayRadius = 16.dp
private val ThumbOutlineRadius = 6.dp

private const val ActiveTrackAlpha = 1f
private const val InactiveTrackAlpha = 0.24f
private const val DisabledActiveTrackAlpha = 0.32f
private const val DisabledInactiveTrackAlpha = 0.12f
private const val ActiveTickMarkAlpha = 0.54f
private const val InactiveTickMarkAlpha = 0.54f
private const val DisabledActiveTickMarkAlpha = 0.12f
private const val DisabledInactiveTickMarkAlpha = 0.12f
private const val ThumbAlpha = 1f
private const val DisabledThumbAlpha = 0.32f
private const val OverlayAlpha = 0.12f

private fun unlerp(value: Float, max: Float, min: Float): Float =
    if (max > min) (value - min) / (max - min) else 0f

private fun discretize(
    value: Int,
    decimals: Int?
): Int {
    if (decimals == null) return value
    val a = value / decimals * decimals
    val b = a + decimals
    return if (value - a > b - value) b else a
}