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

@Composable
fun Popup(
    onCancel: () -> Unit,
    alignment: Alignment = Alignment.TopLeft,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    content: @Composable() () -> Unit
) = composable("Popup") {
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
            onEnd = { _, _ -> onCancel() }
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
                        PressGestureDetector {
                            Card(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(4.dp),
                                children = content
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> PopupMenu(
    onCancel: () -> Unit,
    alignment: Alignment = Alignment.TopLeft,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    items: List<T>,
    onSelected: (T) -> Unit,
    item: @Composable() (T) -> Unit
) = composable("PopupMenu") {
    Popup(
        onCancel = onCancel,
        alignment = alignment,
        offsetX = offsetX,
        offsetY = offsetY
    ) {
        Padding(top = 8.dp, bottom = 8.dp) {
            Column {
                items.forEach { value ->
                    PopupMenuItem(
                        content = { item(value) },
                        onClick = {
                            onSelected(value)
                        }
                    )
                }
            }
        }
    }
}

// todo public
@Composable
private fun PopupMenuItem(
    content: () -> Unit,
    onClick: () -> Unit
) = composable("PopupMenuItem") {
    Ripple(bounded = true) {
        Clickable(
            onClick = onClick,
            children = {
                ConstrainedBox(
                    constraints = DpConstraints(
                        minWidth = 200.dp,
                        minHeight = 48.dp,
                        maxHeight = 48.dp
                    )
                ) {
                    Wrap(Alignment.CenterLeft) {
                        Padding(left = 16.dp, right = 16.dp) {
                            content()
                        }
                    }
                }

            }
        )
    }
}

fun <T> Scaffold.showPopup(
    alignment: Alignment = Alignment.TopLeft,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    onSelected: (T) -> Unit,
    item: @Composable() (T) -> Unit
) {
    showOverlay { dismiss ->
        PopupMenu(
            onCancel = {
                dismiss()
                onCancel?.invoke()
            },
            alignment = alignment,
            offsetX = offsetX,
            offsetY = offsetY,
            items = items,
            onSelected = {
                onSelected(it)
                dismiss()
            },
            item = item
        )
    }
}

fun Scaffold.showPopup(
    alignment: Alignment = Alignment.TopLeft,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    onCancel: (() -> Unit)? = null,
    content: @Composable() (dismiss: () -> Unit) -> Unit
) {
    showOverlay { dismiss ->
        val internalOnCancel = {
            dismiss()
            onCancel?.invoke()
        }
        Popup(
            onCancel = { internalOnCancel() },
            alignment = alignment,
            offsetX = offsetX,
            offsetY = offsetY,
            content = {
                content {
                    internalOnCancel()
                }
            }
        )
    }
}