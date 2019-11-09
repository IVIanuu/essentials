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

package com.ivianuu.essentials.ui.compose.material.slider2

/**
class SliderPosition(initial: Float) {
var value: Float by FramedValue(initial)
internal var onValueChanged: (Float) -> Unit = { value = it }
var controller = SliderValueController(initial, null, onValueChanged)
}

@Composable
fun Slider2(
value: SliderPosition,
onValueChange: (Float) -> Unit = { value.value = it },
minValue: Float = 0f,
maxValue: Float = 1f,
width: Dp? = null,
color: Color = +themeColor { secondary },
tickValues: List<Float> = emptyList()
) {
WithConstraints { constraints ->
val width = +withDensity { width ?: constraints.maxWidth.toDp() }
val max = +withDensity { (width).toPx().value }
value.onValueChanged = {
val fraction = calcFraction(0f, max, it)
onValueChange(lerp(minValue, maxValue, fraction).coerceIn(minValue, maxValue))
}
if (tickValues.isNotEmpty()) {
+memo(tickValues) {
val anchors = tickValues.map { lerp(0f, max,
calcFraction(
minValue,
maxValue,
it
)
) }
value.controller = SliderValueController(value.controller.currentValue,
SliderFlingConfig(value, anchors), value.onValueChanged)
}
}
val anchorsFractions = +memo(tickValues) {
tickValues.map {
calcFraction(
minValue,
maxValue,
it.coerceIn(minValue, maxValue)
)
}
}
var dragging by +state { false }
val callback = DraggableCallback({ dragging = true }, { dragging = false })
PressGestureDetector(onPress = { pos -> value.controller.onDrag(pos.x.value) }) {
Draggable(
dragDirection = DragDirection.Horizontal,
minValue = 0f,
maxValue = max,
valueController = value.controller,
callback = callback
) {
val fraction =
calcFraction(
minValue,
maxValue,
value.value.coerceIn(minValue, maxValue)
)

Container(
height = SliderHeight,
width = width,
expanded = true,
alignment = Alignment.CenterLeft
) {
val thumbSize = ThumbRadius * 2
DrawTrack(
color,
fraction,
anchorsFractions
)
val c = +themeColor { secondary }
val textStyle = +themeTextStyle { caption }
val offset = (width - thumbSize) * fraction
val intOffset = +withDensity { offset.toIntPx() }
if (dragging) {
//                    Popup(
//                        alignment = Alignment.TopLeft,
//                        offset = IntPxPosition(intOffset, -30.ipx)
//                    ) {
//                        Container(width = 40.dp, height = 40.dp, expanded = true) {
//                            DrawShape(CircleShape, c)
//                            Text(value.value.toInt().toString(), textStyle)
//                        }
//                    }
}
Padding(left = offset) {
Ripple(false) {
Container(width = thumbSize, height = thumbSize, expanded = true) {
DrawShape(CircleShape, color)
}
}
}
}
}
}
}
}

@Composable
fun DrawTrack(color: Color, fraction: Float, tickFractions: List<Float>) {
Draw { canvas, parentSize ->
val parentRect = parentSize.toRect()
val thumbPx = ThumbRadius.toPx().value
val centerHeight = parentSize.height.value / 2
val sliderStart = Offset(parentRect.left + thumbPx, centerHeight)
val sliderMax = Offset(parentRect.right - thumbPx, centerHeight)
val paint = Paint().apply {
this.color = color.copy(alpha = 0.32f)
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
tickFractions.groupBy { it > fraction }.forEach { (afterFraction, list) ->
paint.color = if (afterFraction) color else Color.White
val points = list.map {
Offset(
sliderStart.dx + (sliderMax.dx - sliderStart.dx) * it, centerHeight
)
}
canvas.drawPoints(PointMode.points, points, paint)
}
tickFractions.forEach {

}
}
}

private fun calcFraction(a: Float, b: Float, pos: Float) = (pos - a) / (b - a)

private fun SliderValueController(
initial: Float,
flingConfig: FlingConfig?,
onValueChanged: (Float) -> Unit
) =
AnimatedFloatDragController(
animatedFloat = AnimatedFloat(
SliderPositionValueHolder(
initial,
onValueChanged
)
),
flingConfig = flingConfig
)

private fun SliderFlingConfig(value: SliderPosition, anchors: List<Float>): FlingConfig {
val adjustTarget: (Float) -> TargetAnimation? = { _ ->
val now = value.controller.animatedFloat.value
val point = anchors.minBy { abs(it - now) }
val adjusted = point ?: now
TargetAnimation(adjusted,
SliderToTickAnimation
)
}
return FlingConfig(adjustTarget = adjustTarget)
}

private class SliderPositionValueHolder(
var current: Float,
val onValueChanged: (Float) -> Unit
) : ValueHolder<Float> {
override val interpolator: (start: Float, end: Float, fraction: Float) -> Float = ::lerp
override var value: Float
get() = current
set(value) {
current = value
onValueChanged(value)
}
}

val ThumbRadius = 6.dp
val TrackHeight = 2.dp
val SliderHeight = 48.dp
val SliderToTickAnimation = TweenBuilder<Float>().apply { duration = 100 }*/