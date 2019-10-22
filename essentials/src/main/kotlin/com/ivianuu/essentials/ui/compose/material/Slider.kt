package com.ivianuu.essentials.ui.compose.material

import androidx.animation.PhysicsBuilder
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Draw
import androidx.ui.core.PxPosition
import androidx.ui.core.WithConstraints
import androidx.ui.core.dp
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.gesture.RawDragGestureDetector
import androidx.ui.engine.geometry.Offset
import androidx.ui.engine.geometry.Rect
import androidx.ui.foundation.ColoredRect
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.layout.Container
import androidx.ui.layout.Stack
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
        val animValue =
            +animatedFloat(lerp(unlerp(value.toFloat(), min, max), 0, constraints.maxWidth.value))

        fun notifyChangeStart() {
            onChangeStart?.invoke(
                lerp(
                    unlerp(animValue.targetValue, 0, constraints.maxWidth.value),
                    min,
                    max
                ).toInt().coerceIn(min, max)
            )
        }

        val notifiedChangeValue = +memo { NotifiedValue(value) }

        fun notifyChange() {
            val newValue = lerp(
                unlerp(animValue.value, 0, constraints.maxWidth.value),
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
                    unlerp(animValue.targetValue, 0, constraints.maxWidth.value),
                    min,
                    max
                ).toInt().coerceIn(min, max)
            )
        }

        notifyChange()

        RawDragGestureDetector(dragObserver = object : DragObserver {
            override fun onStart(downPosition: PxPosition) {
                super.onStart(downPosition)
                notifyChangeStart()
            }

            override fun onDrag(dragDistance: PxPosition): PxPosition {
                animValue.snapTo(animValue.targetValue + dragDistance.x.value)
                notifyChange()
                return dragDistance
            }

            override fun onStop(velocity: PxPosition) {
                super.onStop(velocity)
                notifyChangeEnd()
            }
        }) {
            PressGestureDetector(
                onPress = { position ->
                    notifyChangeStart()

                    animValue.animateTo(
                        position.x.value,
                        PhysicsBuilder(dampingRatio = 1.0f, stiffness = 1500f)
                    )

                    notifyChange()

                    notifyChangeEnd()
                }
            ) {
                Container(height = 100.dp, expanded = true) {
                    Stack {
                        ColoredRect(Color.Black.copy(alpha = 0.1f))
                        DrawSlider(animValue.value, color)
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawSlider(
    x: Float,
    color: Color
) = composable("DrawSlider") {
    val paint = +memo { Paint() }
    Draw { canvas, parentSize ->
        val centerY = parentSize.height.value / 2
        val constraintX = x.coerceIn(0f, parentSize.width.value)

        // draw bar
        paint.color = color.copy(alpha = 0.12f)
        canvas.drawRect(
            Rect(0f, centerY - 5, parentSize.width.value, centerY + 5),
            paint
        )

        paint.color = color
        canvas.drawRect(
            Rect(0f, centerY - 5, constraintX, centerY + 5),
            paint
        )

        // draw ticker
        canvas.drawCircle(
            Offset(constraintX, centerY), 40f, paint
        )
    }
}

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

