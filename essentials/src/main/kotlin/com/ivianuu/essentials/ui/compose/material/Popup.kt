package com.ivianuu.essentials.ui.compose.material

import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.onActive
import androidx.compose.unaryPlus
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Alignment
import androidx.ui.core.Dp
import androidx.ui.core.Opacity
import androidx.ui.core.WithDensity
import androidx.ui.core.dp
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.Padding
import androidx.ui.layout.Wrap
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import com.ivianuu.essentials.ui.compose.core.composable

fun <T> Scaffold.showPopup(
    alignment: Alignment = Alignment.TopLeft,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    items: List<T>,
    item: @Composable() (T) -> Unit,
    onSelected: (T) -> Unit
) {
    showPopup(
        alignment = alignment,
        offsetX = offsetX,
        offsetY = offsetY
    ) { dismiss ->
        Padding(top = 8.dp, bottom = 8.dp) {
            Column {
                items.forEach { value ->
                    Ripple(bounded = true) {
                        Clickable(
                            onClick = {
                                onSelected(value)
                                dismiss()
                            },
                            children = {
                                ConstrainedBox(
                                    constraints = DpConstraints(
                                        minWidth = 200.dp,
                                        minHeight = 48.dp
                                    )
                                ) {
                                    Wrap(Alignment.CenterLeft) {
                                        Padding(left = 16.dp, right = 16.dp) {
                                            item(value)
                                        }
                                    }
                                }

                            }
                        )
                    }
                }
            }
        }
    }
}

fun Scaffold.showPopup(
    alignment: Alignment = Alignment.TopLeft,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    content: @Composable() (dismiss: () -> Unit) -> Unit
) {
    showOverlay { dismiss ->
        composable("Popup") {
            val alphaAnim = +animatedFloat(0f)

            +onActive {
                alphaAnim.animateTo(
                    targetValue = 1f,
                    anim = TweenBuilder<Float>().apply { duration = 300 }
                )
            }

            val dismissAfterAnim = {
                alphaAnim.animateTo(
                    targetValue = 0f,
                    anim = TweenBuilder<Float>().apply { duration = 300 },
                    onEnd = { _, _ -> dismiss() }
                )
            }

            Opacity(opacity = alphaAnim.value) {
                PressGestureDetector(
                    onPress = {
                        // dismiss on outside touches
                        dismissAfterAnim()
                    }
                ) {
                    WithDensity {
                        Wrap(alignment = alignment) {
                            val padding = when (alignment) {
                                Alignment.TopLeft -> EdgeInsets(left = offsetX, top = offsetY)
                                Alignment.TopCenter -> EdgeInsets(top = offsetY)
                                Alignment.TopRight -> EdgeInsets(right = offsetX, top = offsetY)
                                Alignment.CenterLeft -> EdgeInsets(left = offsetX)
                                Alignment.Center -> EdgeInsets()
                                Alignment.CenterRight -> EdgeInsets(right = offsetX)
                                Alignment.BottomLeft -> EdgeInsets(left = offsetX, bottom = offsetY)
                                Alignment.BottomCenter -> EdgeInsets(bottom = offsetY)
                                Alignment.BottomRight -> EdgeInsets(
                                    right = offsetX,
                                    bottom = offsetY
                                )
                            }
                            Padding(padding = padding) {
                                Padding(4.dp) {
                                    PressGestureDetector {
                                        Card(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(4.dp),
                                            children = {
                                                content {
                                                    dismissAfterAnim()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}