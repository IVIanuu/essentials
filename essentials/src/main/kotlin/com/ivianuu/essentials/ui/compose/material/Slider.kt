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

/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.animation.AnimationEndReason
import androidx.animation.TargetAnimation
import androidx.animation.TweenBuilder
import androidx.annotation.IntRange
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Draw
import androidx.ui.core.PxSize
import androidx.ui.core.WithConstraints
import androidx.ui.core.ambientDensity
import androidx.ui.core.dp
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.px
import androidx.ui.core.toRect
import androidx.ui.core.withDensity
import androidx.ui.engine.geometry.Offset
import androidx.ui.foundation.animation.AnimatedValueHolder
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.PaintingStyle
import androidx.ui.graphics.PointMode
import androidx.ui.graphics.StrokeCap
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.lerp
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeColor
import androidx.ui.semantics.Semantics
import androidx.ui.semantics.accessibilityValue
import kotlin.math.abs

// todo remove once added

/**
 * State for Slider that represents the Slider value, its bounds and discrete values that Slider
 * can have (if set).
 *
 * @param initial initial value for the Slider when created
 * @param valueRange range of values that Slider value can take
 * @param discreteValues if non-empty, defines the set of possible values that Slider can have
 * within the range provided. Values outside of the specified range will be ignored.
 * You must put the start and the end of the range as discrete values as well if you want
 * to allow people to choose them as discrete values.
 */
class SliderPosition(
    initial: Float = 0f,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    discreteValues: Set<Float> = emptySet()
) {

    internal val minValue: Float = valueRange.start
    internal val maxValue: Float = valueRange.endInclusive

    init {
        require(!discreteValues.contains(Float.NaN)) {
            "discrete value cannot be NaN"
        }
    }

    /**
     * Current Slider value
     */
    var value: Float
        get() = scale(
            minPx,
            maxPx,
            holder.value,
            minValue,
            maxValue
        )
        set(value) {
            holder.animatedFloat.snapTo(
                scale(
                    minValue,
                    maxValue,
                    value,
                    minPx,
                    maxPx
                )
            )
        }

    private var maxPx = Float.MAX_VALUE
    private var minPx = Float.MIN_VALUE

    internal fun setBounds(min: Float, max: Float) {
        val newValue =
            scale(minPx, maxPx, holder.value, min, max)
        minPx = min
        maxPx = max
        holder.setBounds(min, max)
        holder.animatedFloat.snapTo(newValue)
    }

    internal val holder = AnimatedValueHolder(
        scale(
            minValue,
            maxValue,
            initial,
            minPx,
            maxPx
        )
    )

    internal val tickFractions: List<Float> =
        discreteValues
            .filter { it in valueRange }
            .map { calcFraction(minValue, maxValue, it) }
            .distinct()
}

/**
 * Function to create Slider position with steps amount of discrete values evenly distributed
 * across the whole value range.
 *
 * @param initial initial value for the Slider when created
 * @param valueRange range of values that Slider value can take
 * @param steps amounts of discrete values, evenly distributed between across the whole value range
 */
fun SliderPosition(
    initial: Float = 0f,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int
): SliderPosition {
    val minValue: Float = valueRange.start
    val maxValue: Float = valueRange.endInclusive
    val discreteValues = if (steps == 0) {
        emptySet()
    } else {
        val stepSize = (maxValue - minValue) / (steps + 1)
        (listOf(minValue, maxValue) union
                List(steps) { index -> minValue + stepSize * (index + 1) }).toSet()
    }
    return SliderPosition(
        initial,
        valueRange,
        discreteValues
    )
}

/**
 * Sliders allow users to make selections from a range of values.
 *
 * Sliders reflect a range of values along a bar, from which users may select a single value.
 * They are ideal for adjusting settings such as volume, brightness, or applying image filters.
 *
 * @sample androidx.ui.material.samples.SliderSample
 *
 * You can allow the user to choose only between predefined set of values by providing
 * discrete values in [SliderPosition].
 *
 * You can do it by specifying the amount of steps between min and max values:
 *
 * @sample androidx.ui.material.samples.StepsSliderSample
 *
 * You can also specify custom discrete values within the given range:
 *
 * @sample androidx.ui.material.samples.DiscreteSliderSample
 *
 * @param position [SliderPosition] object to represent value of the Slider
 * @param onValueChange lambda in which value should be updated
 * @param onValueChangeEnd lambda to be invoked when value change has ended. Use this method to
 * update preferences by reading the current value from SliderPosition
 * @param color color of the Slider
 */
@Composable
fun Slider(
    position: SliderPosition,
    onValueChange: (Float) -> Unit = { position.value = it },
    onValueChangeEnd: () -> Unit = {},
    color: Color = +themeColor { primary }
) {
    Container(height = SliderHeight, expanded = true) {
        WithConstraints { constraints ->
            val maxPx = constraints.maxWidth.value.toFloat()
            val minPx = 0f
            +memo(minPx, maxPx, position) { position.setBounds(minPx, maxPx) }

            fun Float.toSliderPosition(): Float =
                scale(
                    minPx,
                    maxPx,
                    this,
                    position.minValue,
                    position.maxValue
                )

            val flingConfig =
                if (position.tickFractions.isNotEmpty()) {
                    val anchorsPx = position.tickFractions.map { lerp(minPx, maxPx, it) }
                    SliderFlingConfig(
                        position,
                        anchorsPx
                    ) { endValue ->
                        onValueChange(endValue.toSliderPosition())
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
            val pressed = +state { false }
            PressGestureDetector(
                onPress = { pos ->
                    onValueChange(pos.x.value.toSliderPosition())
                    pressed.value = true
                },
                onRelease = {
                    pressed.value = false
                    gestureEndAction(0f)
                }) {
                Draggable(
                    dragDirection = DragDirection.Horizontal,
                    dragValue = position.holder,
                    onDragStarted = { pressed.value = true },
                    onDragValueChangeRequested = { onValueChange(it.toSliderPosition()) },
                    onDragStopped = { velocity ->
                        pressed.value = false
                        gestureEndAction(velocity)
                    },
                    children = {
                        SliderImpl(
                            position,
                            color,
                            maxPx,
                            pressed.value
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SliderImpl(position: SliderPosition, color: Color, width: Float, pressed: Boolean) {
    val widthDp = withDensity(+ambientDensity()) {
        width.px.toDp()
    }
    Semantics(container = true, properties = { accessibilityValue = "${position.value}" }) {
        Container(expanded = true, alignment = Alignment.CenterLeft) {
            val thumbSize = ThumbRadius * 2
            val fraction = with(position) {
                calcFraction(
                    minValue,
                    maxValue,
                    this.value
                ).coerceIn(0f, 1f)
            }
            val offset = (widthDp - thumbSize) * fraction
            DrawTrack(color, position)
            Padding(left = offset) {
                Ripple(false) {
                    Surface(
                        shape = CircleShape,
                        color = color,
                        elevation = if (pressed) 6.dp else 1.dp
                    ) {
                        Container(width = thumbSize, height = thumbSize, expanded = true) {}
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
    val activeTickColor = +themeColor { onPrimary.copy(alpha = TickColorAlpha) }
    val inactiveTickColor = color.copy(alpha = TickColorAlpha)
    Draw { canvas: Canvas, parentSize: PxSize ->
        val fraction = with(position) {
            calcFraction(minValue, maxValue, this.value)
                .coerceIn(0f, 1f)
        }
        val parentRect = parentSize.toRect()
        val thumbPx = ThumbRadius.toPx().value
        val centerHeight = parentSize.height.value / 2
        val sliderStart = Offset(parentRect.left + thumbPx, centerHeight)
        val sliderMax = Offset(parentRect.right - thumbPx, centerHeight)
        val paint = Paint().apply {
            this.color = color.copy(alpha = InactiveTrackColorAlpha)
            this.isAntiAlias = true
            this.strokeCap = StrokeCap.round
            this.strokeWidth = TrackHeight.toPx().value
            this.style = PaintingStyle.stroke
        }
        canvas.drawLine(sliderStart, sliderMax, paint)
        val sliderValue = Offset(
            sliderStart.dx + (sliderMax.dx - sliderStart.dx) * fraction,
            centerHeight
        )
        paint.color = color
        canvas.drawLine(sliderStart, sliderValue, paint)
        position.tickFractions.groupBy { it > fraction }.forEach { (afterFraction, list) ->
            paint.color = if (afterFraction) inactiveTickColor else activeTickColor
            val points = list.map {
                Offset(lerp(sliderStart, sliderMax, it).dx, centerHeight)
            }
            canvas.drawPoints(PointMode.points, points, paint)
        }
    }
}

private fun lerp(a: Offset, b: Offset, x: Float) =
    Offset(lerp(a.dx, b.dx, x), lerp(a.dy, b.dy, x))

private fun scale(a1: Float, b1: Float, x1: Float, a2: Float, b2: Float) =
    lerp(a2, b2, calcFraction(a1, b1, x1))

private fun calcFraction(a: Float, b: Float, pos: Float) =
    if (b - a == 0f) 0f else (pos - a) / (b - a)

private fun SliderFlingConfig(
    value: SliderPosition,
    anchors: List<Float>,
    onSuccessfulEnd: (Float) -> Unit
): FlingConfig {
    val adjustTarget: (Float) -> TargetAnimation? = { _ ->
        val now = value.holder.value
        val point = anchors.minBy { abs(it - now) }
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
private val InactiveTrackColorAlpha = 0.24f
private val TickColorAlpha = 0.54f
private val SliderToTickAnimation = TweenBuilder<Float>().apply { duration = 100 }