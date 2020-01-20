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

package com.ivianuu.essentials.ui.material

import androidx.animation.AnimationEndReason
import androidx.animation.TargetAnimation
import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Draw
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.foundation.ValueHolder
import androidx.ui.foundation.animation.AnimatedValueHolder
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.PaintingStyle
import androidx.ui.graphics.PointMode
import androidx.ui.graphics.StrokeCap
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import androidx.ui.semantics.Semantics
import androidx.ui.semantics.accessibilityValue
import androidx.ui.unit.PxSize
import androidx.ui.unit.dp
import androidx.ui.unit.lerp
import androidx.ui.unit.px
import androidx.ui.unit.toRect
import com.ivianuu.essentials.ui.common.withDensity
import com.ivianuu.essentials.ui.core.ambientOf
import com.ivianuu.essentials.ui.core.current
import kotlin.math.abs

// todo remove once fixed in compose

@Immutable
data class SliderStyle(val color: Color)

val SliderStyleAmbient =
    ambientOf<SliderStyle?> { null }

@Composable
fun DefaultSliderStyle(color: Color = MaterialTheme.colors().secondary) =
    SliderStyle(color = color)

class SliderPosition(
    initial: Float = 0f,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    val steps: Int = 0
) {

    internal val startValue: Float = valueRange.start
    internal val endValue: Float = valueRange.endInclusive

    var value
        get() = holder.value
        set(value) {
            holder.animatedFloat.snapTo(value)
        }

    internal val holder = AnimatedValueHolder(initial).apply {
        setBounds(startValue, endValue)
    }

    internal val tickValues: List<Float> =
        if (steps == 0) emptyList() else List(steps + 1) { step ->
            startValue + (endValue - startValue) / steps * step
        }

    init {
        require(steps >= 0) {
            "steps should be >= 0"
        }
    }
}

@Composable
fun Slider(
    position: SliderPosition,
    onValueChange: (Float) -> Unit = { position.value = it },
    modifier: Modifier = Modifier.None,
    onValueChangeEnd: () -> Unit = {},
    style: SliderStyle = SliderStyleAmbient.current ?: DefaultSliderStyle()
) {
    Container(modifier = modifier) {
        val maxPx = state { 0f }
        Draw { _, parentSize ->
            if (parentSize.width.value != maxPx.value) {
                maxPx.value = parentSize.width.value
            }
        }
        val minPx = 0f

        fun Float.toSliderPosition(): Float =
            scale(minPx, maxPx.value, this, position.startValue, position.endValue)

        val flingConfig =
            if (position.tickValues.isNotEmpty()) {
                SliderFlingConfig(position, position.tickValues) { endValue ->
                    onValueChange(endValue)
                    onValueChangeEnd()
                }
            } else {
                null
            }
        val gestureEndAction = { velocity: Float ->
            if (flingConfig != null) {
                position.holder.fling(flingConfig, velocity)
            } else {
                onValueChangeEnd()
            }
        }
        val pressed = state { false }
        PressGestureDetector(
            onPress = { pos ->
                onValueChange(pos.x.value.toSliderPosition())
                pressed.value = true
            },
            onRelease = {
                pressed.value = false
                gestureEndAction(0f)
            }
        ) {
            // todo ir
            val dragValue = remember(position) {
                object : ValueHolder<Float> {
                    override val value: Float
                        get() = scale(position.startValue, position.endValue, position.value, minPx, maxPx.value)
                }
            }
            Draggable(
                dragDirection = DragDirection.Horizontal,
                dragValue = dragValue,
                onDragStarted = { pressed.value = true },
                onDragValueChangeRequested = { onValueChange(it.toSliderPosition()) },
                onDragStopped = { velocity ->
                    pressed.value = false
                    gestureEndAction(velocity)
                },
                children = { SliderImpl(position, style.color, maxPx.value, pressed.value) }
            )
        }
    }
}

@Composable
private fun SliderImpl(
    position: SliderPosition,
    color: Color,
    width: Float,
    pressed: Boolean
) {
    val widthDp = withDensity { width.px.toDp() }
    Semantics(container = true, properties = { accessibilityValue = "${position.value}" }) {
        Container(
            expanded = true,
            constraints = DefaultSliderConstraints,
            alignment = Alignment.CenterLeft
        ) {
            val thumbSize = ThumbRadius * 2
            val fraction = with(position) { calcFraction(startValue, endValue, value) }
            val offset = (widthDp - thumbSize) * fraction
            DrawTrack(color, position)
            Container(
                modifier = LayoutPadding(left = offset)
            ) {
                Ripple(bounded = false) {
                    Surface(
                        shape = CircleShape,
                        color = color,
                        modifier = LayoutSize(thumbSize, thumbSize),
                        elevation = if (pressed) 6.dp else 1.dp
                    ) {
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawTrack(
    color: Color,
    position: SliderPosition
) {
    val activeTickColor = MaterialTheme.colors().onPrimary.copy(alpha = TickColorAlpha)
    val inactiveTickColor = color.copy(alpha = TickColorAlpha)
    val paint = remember {
        Paint().apply {
            this.isAntiAlias = true
            this.strokeCap = StrokeCap.round
            this.style = PaintingStyle.stroke
        }
    }
    Draw { canvas: Canvas, parentSize: PxSize ->
        paint.strokeWidth = TrackHeight.toPx().value
        val fraction = with(position) { calcFraction(startValue, endValue, value) }
        val parentRect = parentSize.toRect()
        val thumbPx = ThumbRadius.toPx().value
        val centerHeight = parentSize.height.value / 2
        val sliderStart = Offset(parentRect.left + thumbPx, centerHeight)
        val sliderMax = Offset(parentRect.right - thumbPx, centerHeight)
        paint.color = color.copy(alpha = InactiveTrackColorAlpha)
        canvas.drawLine(sliderStart, sliderMax, paint)
        val sliderValue = Offset(
            sliderStart.dx + (sliderMax.dx - sliderStart.dx) * fraction,
            centerHeight
        )
        paint.color = color
        canvas.drawLine(sliderStart, sliderValue, paint)

        position.tickValues
            .groupBy { it > position.value }
            .mapValues { (_, values) ->
                values.map { value ->
                    calcFraction(position.startValue, position.endValue, value)
                }
            }
            .forEach { (afterFraction, list) ->
                paint.color = if (afterFraction) inactiveTickColor else activeTickColor
                val points = list.map { fraction ->
                    Offset(Offset.lerp(sliderStart, sliderMax, fraction).dx, centerHeight)
                }
                canvas.drawPoints(PointMode.points, points, paint)
            }
    }
}

// Scale x1 from a1..b1 range to a2..b2 range
private fun scale(a1: Float, b1: Float, x1: Float, a2: Float, b2: Float) =
    lerp(a2.px, b2.px, calcFraction(a1, b1, x1)).value

// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

private fun SliderFlingConfig(
    position: SliderPosition,
    tickValues: List<Float>,
    onSuccessfulEnd: (Float) -> Unit
): FlingConfig {
    val adjustTarget: (Float) -> TargetAnimation? = { _ ->
        val now = position.value
        val point = tickValues.minBy { abs(it - now) }
        val adjusted = point ?: now
        TargetAnimation(adjusted, SliderToTickAnimation)
    }
    return FlingConfig(
        adjustTarget = adjustTarget,
        onAnimationEnd = { reason, endValue, _ ->
            if (reason != AnimationEndReason.Interrupted) {
                onSuccessfulEnd(endValue)
            }
        }
    )
}

private val ThumbRadius = 6.dp
private val TrackHeight = 4.dp
private val SliderHeight = 48.dp
private val SliderMinWidth = 144.dp
private val DefaultSliderConstraints =
    DpConstraints(minWidth = SliderMinWidth, maxHeight = SliderHeight)
private val InactiveTrackColorAlpha = 0.24f
private val TickColorAlpha = 0.54f
private val SliderToTickAnimation = TweenBuilder<Float>().apply { duration = 100 }
