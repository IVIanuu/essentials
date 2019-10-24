package com.ivianuu.essentials.ui.compose.material

import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.memo
import androidx.compose.onActive
import androidx.compose.unaryPlus
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Alignment
import androidx.ui.core.Dp
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.OnPositioned
import androidx.ui.core.Opacity
import androidx.ui.core.WithDensity
import androidx.ui.core.dp
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.positionInRoot
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Image
import androidx.ui.layout.Column
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.Padding
import androidx.ui.layout.Wrap
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.resources.drawableResource

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
                                shape = RoundedCornerShape(4.dp)
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
                    }
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

fun <T> Scaffold.showPopupMenu(
    alignment: Alignment = Alignment.TopLeft,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    onSelected: (T) -> Unit,
    item: @Composable() (T) -> Unit
) {
    addOverlay { dismiss ->
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

private class CoordinatesHolder(var coordinates: LayoutCoordinates?)

// todo better name?
@Composable
fun <T> PopupMenuTrigger(
    alignment: Alignment = Alignment.Center,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    onSelected: (T) -> Unit,
    item: @Composable() (T) -> Unit,
    child: @Composable() (showPopup: () -> Unit) -> Unit
) = composable("PopupMenuTrigger") {
    Wrap {
        WithDensity {
            // cache coordinates
            val coordinatesHolder = +memo { CoordinatesHolder(null) }
            OnPositioned { coordinatesHolder.coordinates = it }

            val scaffold = +ambient(ScaffoldAmbient)

            child {
                val coordinates = coordinatesHolder.coordinates!!
                val scaffoldCoordinates = scaffold.coordinates!!

                val globalPosition = coordinates.positionInRoot
                val scaffoldGlobalPosition = scaffoldCoordinates.positionInRoot

                val width = coordinates.size.width.toDp()
                val height = coordinates.size.height.toDp()
                val halfWidth = width.div(2)
                val halfHeight = height.div(2)
                val left = globalPosition.x.minus(scaffoldGlobalPosition.x).toDp()
                val top = globalPosition.y.minus(scaffoldGlobalPosition.y).toDp()
                val right = left.plus(width)
                val bottom = top.plus(height)
                val centerX = left.plus(halfWidth)
                val centerY = top.plus(halfHeight)

                val isLeft = left.value < scaffoldCoordinates.position.x.plus(
                    scaffoldCoordinates.size.width.div(2)
                ).toDp().value
                val isTop = top.value < scaffoldCoordinates.position.y.plus(
                    scaffoldCoordinates.size.height.div(2)
                ).toDp().value

                val realAlignment = if (isLeft) {
                    if (isTop) {
                        Alignment.TopLeft
                    } else {
                        Alignment.BottomLeft
                    }
                } else {
                    if (isTop) {
                        Alignment.TopRight
                    } else {
                        Alignment.BottomRight
                    }
                }

                var (realOffsetX, realOffsetY) = when (alignment) {
                    Alignment.TopLeft -> left to top
                    Alignment.TopCenter -> centerX to top
                    Alignment.TopRight -> right to top
                    Alignment.CenterLeft -> left to centerY
                    Alignment.Center -> centerX to centerY
                    Alignment.CenterRight -> right to centerY
                    Alignment.BottomLeft -> left to bottom
                    Alignment.BottomCenter -> centerX to bottom
                    Alignment.BottomRight -> right to bottom
                }

                if (!isLeft) {
                    realOffsetX = scaffoldCoordinates.size.width.toDp().minus(realOffsetX)
                }
                if (!isTop) {
                    realOffsetY = scaffoldCoordinates.size.height.toDp().minus(realOffsetY)
                }

                when (alignment) {
                    Alignment.TopLeft -> {
                        realOffsetX = realOffsetX.plus(offsetX)
                        realOffsetY = realOffsetY.plus(offsetY)
                    }
                    Alignment.TopCenter -> {
                        realOffsetY = realOffsetY.plus(offsetY)
                    }
                    Alignment.TopRight -> {
                        realOffsetX = realOffsetX.minus(offsetX)
                        realOffsetY = realOffsetY.plus(offsetY)
                    }
                    Alignment.CenterLeft -> {
                        realOffsetX = realOffsetX.plus(offsetX)
                    }
                    Alignment.Center -> {
                    }
                    Alignment.CenterRight -> {
                        realOffsetX = realOffsetX.minus(offsetX)
                    }
                    Alignment.BottomLeft -> {
                        realOffsetX = realOffsetX.plus(offsetX)
                        realOffsetY = realOffsetY.minus(offsetY)
                    }
                    Alignment.BottomCenter -> {
                        realOffsetY = realOffsetY.minus(offsetY)
                    }
                    Alignment.BottomRight -> {
                        realOffsetX = realOffsetX.minus(offsetX)
                        realOffsetY = realOffsetY.minus(offsetY)
                    }
                }

                scaffold.showPopupMenu(
                    alignment = realAlignment,
                    offsetX = realOffsetX,
                    offsetY = realOffsetY,
                    items = items,
                    item = item,
                    onCancel = onCancel,
                    onSelected = onSelected
                )
            }
        }
    }
}

@Composable
fun <T> PopupMenuButton(
    alignment: Alignment = Alignment.Center,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    onCancel: (() -> Unit)? = null,
    items: List<T>,
    onSelected: (T) -> Unit,
    item: @Composable() (T) -> Unit,
    icon: Image = +drawableResource(R.drawable.abc_ic_menu_overflow_material)
) = composable("PopupMenuButton") {
    PopupMenuTrigger(
        alignment = alignment,
        offsetX = offsetX,
        offsetY = offsetY,
        onCancel = onCancel,
        items = items,
        onSelected = onSelected,
        item = item
    ) { showPopup ->
        ImageButton(
            image = icon,
            onClick = showPopup
        )
    }
}