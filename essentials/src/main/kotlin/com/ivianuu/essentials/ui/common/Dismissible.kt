package com.ivianuu.essentials.ui.common

import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.remember
import androidx.ui.core.DensityAmbient
import androidx.ui.core.OnChildPositioned
import androidx.ui.foundation.animation.AnimatedValueHolder
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.Draggable
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Stack
import androidx.ui.unit.Dp
import androidx.ui.unit.IntPxSize
import androidx.ui.unit.dp
import androidx.ui.unit.px
import com.ivianuu.essentials.ui.layout.LayoutOffset
import kotlin.time.milliseconds

@Composable
fun Dismissible(
    onDismiss: () -> Unit,
    background: @Composable (() -> Unit)? = null,
    children: @Composable () -> Unit,
) {
    val state = remember { DismissibleState() }

    Draggable(
        dragDirection = DragDirection.Horizontal,
        dragValue = state.dragValue,
        onDragValueChangeRequested = { state.dragValue.animatedFloat.snapTo(it) },
        onDragStopped = {
            val currentValue = state.dragValue.value
            val dismissThresholdPx = state.realChildSize!!.width * state.dismissThreshold
            val dismissThresholdReached =
                if (currentValue > 0f) currentValue >= dismissThresholdPx.value
                else currentValue <= -dismissThresholdPx.value
            if (dismissThresholdReached) {
                val targetValue = (if (currentValue > 0f) state.realChildSize!!.width
                else -state.realChildSize!!.width).value.toFloat()
                state.dragValue.animatedFloat.animateTo(
                    targetValue = targetValue,
                    onEnd = { _, _ ->
                        state.sizeValue.animatedFloat.animateTo(
                            targetValue = 0f,
                            anim = TweenBuilder<Float>().apply {
                                duration = 300.milliseconds.toLongMilliseconds().toInt()
                            },
                            onEnd = { _, _ ->
                                onDismiss()
                            },
                        )
                    },
                )
            } else {
                state.dragValue.animatedFloat.animateTo(0f)
            }
        },
    ) {
        OnChildPositioned(
            onPositioned = { coords ->
                if (coords.size != state.realChildSize && state.sizeValue.value == 1f) {
                    state.realChildSize = coords.size
                }
            },
        ) {
            val density = DensityAmbient.current
            Stack(
                modifier = LayoutSize.Constrain(
                    minWidth = 0.dp,
                    maxWidth = Dp.Infinity,
                    minHeight = 0.dp,
                    maxHeight = if (state.realChildSize == null) Dp.Infinity else with(density) {
                        state.realChildSize!!.height.toDp() * state.sizeValue.value
                    },
                ),
            ) {
                if (background != null) {
                    Container(modifier = LayoutGravity.Stretch) {
                        background()
                    }
                }

                Container(
                    modifier = LayoutOffset(
                        offsetX = with(density) {
                            state.dragValue.value.px.toDp()
                        },
                    ),
                    children = children,
                )
            }
        }
    }
}

@Model
private class DismissibleState {
    val dismissThreshold = 0.4f

    var realChildSize: IntPxSize? = null

    val dragValue = AnimatedValueHolder(0f)

    val sizeValue = AnimatedValueHolder(1f)
}
