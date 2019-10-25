package com.ivianuu.essentials.ui.compose.material

import androidx.animation.AnimatedFloat
import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.State
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
import androidx.ui.lerp
import androidx.ui.material.themeColor
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
        val internalValue = +state { 0f }
        val overlayAnim = +animatedFloat(0f)
        val state = +memo { SliderState(internalValue, overlayAnim) }

        // update state
        +memo(
            value,
            onChanged,
            onChangeStart,
            onChangeEnd,
            min,
            max,
            divisions,
            color,
            constraints.maxWidth.value
        ) {
            state.update(
                value = value,
                onChangeStart = onChangeStart,
                onChanged = onChanged,
                onChangeEnd = onChangeEnd,
                min = min,
                max = max,
                divisions = divisions,
                maxWidth = constraints.maxWidth.value
            )
        }

        RawDragGestureDetector(
            dragObserver = object : DragObserver {
                override fun onStart(downPosition: PxPosition) {
                    super.onStart(downPosition)
                    state.handleDragStart()
                }

                override fun onDrag(dragDistance: PxPosition): PxPosition {
                    state.handleDrag(dragDistance)
                    return dragDistance
                }

                override fun onStop(velocity: PxPosition) {
                    super.onStop(velocity)
                    state.handleDragEnd()
                }
            }
        ) {
            PressGestureDetector(
                onPress = { state.handlePress(it) }
            ) {
                Container(height = ContainerHeight, expanded = true) {
                    DrawSlider(
                        state.getCurrentFraction(),
                        overlayAnim.value,
                        color,
                        +themeColor { surface },
                        divisions,
                        state.isEnabled
                    )
                }
            }
        }
    }
}

private class SliderState(
    private val internalValue: State<Float>,
    private val overlayAnim: AnimatedFloat
) {

    private var onChangeStart: ((Int) -> Unit)? = null
    private var onChanged: ((Int) -> Unit)? = null
    private var onChangeEnd: ((Int) -> Unit)? = null
    private var min = 0
    private var max = 0
    private var divisions: Int? = null

    private var maxWidth = 0

    val isEnabled get() = onChanged != null

    private var lastNotifiedValue: Int? = null

    fun update(
        value: Int,
        onChangeStart: ((Int) -> Unit)?,
        onChanged: ((Int) -> Unit)?,
        onChangeEnd: ((Int) -> Unit)?,
        min: Int,
        max: Int,
        divisions: Int?,
        maxWidth: Int
    ) {
        this.onChangeStart = onChangeStart
        this.onChanged = onChanged
        this.onChangeEnd = onChangeEnd
        this.min = min
        this.max = max
        this.divisions = divisions
        this.maxWidth = maxWidth

        val fraction = unlerp(value.toFloat(), min.toFloat(), max.toFloat())
            .coerceIn(min.toFloat(), max.toFloat())
        val newValue = lerp(0f, maxWidth.toFloat(), fraction)
        internalValue.value = newValue
        lastNotifiedValue = getCurrentUserValue()
    }

    fun handleDragStart() {
        if (isEnabled) {
            overlayAnim.animateTo(
                targetValue = 1f,
                anim = TweenBuilder<Float>().apply { duration = OverlayAnimDuration }
            )

            onChangeStart?.invoke(getCurrentUserValue())
        }
    }

    fun handleDrag(dragDistance: PxPosition) {
        if (isEnabled) {
            internalValue.value = internalValue.value + dragDistance.x.value
            notifyChange()
        }
    }

    fun handlePress(position: PxPosition) {
        if (isEnabled) {
            handleDragStart()
            internalValue.value = position.x.value
            notifyChange()
            handleDragEnd()
        }
    }

    fun handleDragEnd() {
        if (isEnabled) {
            overlayAnim.animateTo(
                targetValue = 0f,
                anim = TweenBuilder<Float>().apply { duration = OverlayAnimDuration }
            )

            onChangeEnd?.invoke(getCurrentUserValue())
        }
    }

    private fun notifyChange() {
        val currentValue = getCurrentUserValue()
        if (lastNotifiedValue != currentValue) {
            lastNotifiedValue = currentValue
            onChanged?.invoke(currentValue)
        }
    }

    fun getCurrentFraction(): Float =
        unlerp(internalValue.value, 0f, maxWidth.toFloat())
            .coerceIn(0f, 1f)

    private fun getCurrentUserValue(): Int {
        return lerp(min, max, getCurrentFraction())
            .coerceIn(min, max)
            .let { value ->
                discretize(value, divisions?.let { divisions ->
                    max / divisions
                })
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
    isEnabled: Boolean
) = composable("DrawSlider") {
    val paint = +memo { Paint() }

    Opacity(opacity = if (isEnabled) 1f else 0.5f) {
        Draw { canvas, parentSize ->
            val thumbOverlayRadius = OverlayRadius.toPx().value

            val centerY = parentSize.height.value / 2
            val startX = thumbOverlayRadius
            val endX = parentSize.width.value - thumbOverlayRadius
            val currentX = lerp(0f, parentSize.width.value, fraction)
                .let { tmp ->
                    if (divisions != null) {
                        discretize(tmp.toInt(), endX.toInt() / divisions).toFloat()
                            .coerceIn(startX, endX)
                    } else {
                        tmp
                    }
                }.coerceIn(startX, endX)


            val trackHeight = TrackHeight.toPx().value

            // track
            paint.color =
                color.copy(alpha = if (isEnabled) InactiveTrackAlpha else DisabledInactiveTrackAlpha)
            canvas.drawRect(
                Rect(startX, centerY - trackHeight / 2, endX, centerY + trackHeight / 2),
                paint
            )

            // active track
            paint.color =
                color.copy(alpha = if (isEnabled) ActiveTrackAlpha else DisabledActiveTrackAlpha)
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
                            alpha = if (isEnabled) ActiveTickMarkAlpha else DisabledActiveTickMarkAlpha
                        )
                    } else {
                        paint.color = color.copy(
                            alpha = if (isEnabled) InactiveTickMarkAlpha else DisabledInactiveTickMarkAlpha
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
            if (!isEnabled) {
                paint.color = surfaceColor
                canvas.drawCircle(Offset(currentX, centerY), ThumbOutlineRadius.toPx().value, paint)
            }

            // thumb
            val thumbRadius =
                if (isEnabled) ThumbRadius.toPx().value else DisabledThumbRadius.toPx().value
            paint.color = color.copy(alpha = if (isEnabled) ThumbAlpha else DisabledThumbAlpha)
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

private val OverlayAnimDuration = 250

private fun unlerp(value: Float, min: Float, max: Float): Float =
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