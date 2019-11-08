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

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Dp
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.OnPositioned
import androidx.ui.core.WithDensity
import androidx.ui.core.dp
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.globalPosition
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
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.ref
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade

fun <T> popupMenuRoute(
    alignment: Alignment,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    items: List<T>,
    onSelected: (T) -> Unit,
    onCancel: (() -> Unit)? = null,
    item: @Composable() (T) -> Unit
) = composeControllerRoute(
    options = controllerRouteOptions().fade(removesFromViewOnPush = false)
) {
    val navigator = +inject<Navigator>()

    val dismiss: (Boolean) -> Unit = { cancelled ->
        navigator.pop()
        if (cancelled) onCancel?.invoke()
    }

    PressGestureDetector(
        onPress = { dismiss(true) }
    ) {
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
                    PopupMenu(
                        items = items,
                        onSelected = {
                            dismiss(false)
                            onSelected(it)
                        },
                        item = item
                    )
                }
            }
        }
    }
}

@Composable
fun <T> PopupMenu(
    items: List<T>,
    onSelected: (T) -> Unit,
    item: @Composable() (T) -> Unit
) = composable("PopupMenu") {
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

// todo better name?
@Composable
fun <T> PopupMenuTrigger(
    alignment: Alignment = Alignment.TopLeft,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    items: List<T>,
    onSelected: (T) -> Unit,
    onCancel: (() -> Unit)? = null,
    item: @Composable() (T) -> Unit,
    child: @Composable() (showPopup: () -> Unit) -> Unit
) = composable("PopupMenuTrigger") {
    WithDensity {
        val navigator = +inject<Navigator>()

        val coordinatesHolder = +ref<LayoutCoordinates?> { null }
        OnPositioned { coordinatesHolder.value = it }

        val showPopup = {
            val coordinates = coordinatesHolder.value!!

            var rootCoordinates = coordinates
            while (rootCoordinates.parentCoordinates != null) {
                rootCoordinates = rootCoordinates.parentCoordinates!!
            }

            val rootWidth = rootCoordinates.size.width.toDp()
            val rootHeight = rootCoordinates.size.height.toDp()
            val rootLeft = rootCoordinates.position.x.toDp()
            val rootTop = rootCoordinates.position.y.toDp()
            val rootRight = rootLeft + rootWidth
            val rootBottom = rootTop + rootHeight

            val width = coordinates.size.width.toDp()
            val height = coordinates.size.height.toDp()
            val halfWidth = width / 2
            val halfHeight = height / 2
            val left = coordinates.globalPosition.x.toDp()
            val top = coordinates.globalPosition.y.toDp()
            val right = left + width
            val bottom = top + height
            val centerX = left + halfWidth
            val centerY = top + halfHeight

            val isLeft = left < rootLeft + rootWidth / 2
            val isTop = top < rootTop + rootHeight / 2

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
                realOffsetX = rootRight - realOffsetX
            }
            if (!isTop) {
                realOffsetY = rootBottom - realOffsetY
            }

            when (alignment) {
                Alignment.TopLeft -> {
                    realOffsetX += offsetX
                    realOffsetY += offsetY
                }
                Alignment.TopCenter -> {
                    realOffsetY += offsetY
                }
                Alignment.TopRight -> {
                    realOffsetX -= offsetX
                    realOffsetY += offsetY
                }
                Alignment.CenterLeft -> {
                    realOffsetX += offsetX
                }
                Alignment.Center -> {
                }
                Alignment.CenterRight -> {
                    realOffsetX -= offsetX
                }
                Alignment.BottomLeft -> {
                    realOffsetX += offsetX
                    realOffsetY -= offsetY
                }
                Alignment.BottomCenter -> {
                    realOffsetY -= offsetY
                }
                Alignment.BottomRight -> {
                    realOffsetX -= offsetX
                    realOffsetY -= offsetY
                }
            }

            navigator.push(
                popupMenuRoute(
                    onCancel = onCancel,
                    alignment = realAlignment,
                    offsetX = realOffsetX,
                    offsetY = realOffsetY,
                    items = items,
                    item = item,
                    onSelected = onSelected
                )
            )
        }

        child(showPopup)
    }
}