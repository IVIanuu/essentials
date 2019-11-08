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
import androidx.compose.View
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.IntPx
import androidx.ui.core.IntPxPosition
import androidx.ui.core.Layout
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.OnPositioned
import androidx.ui.core.PxPosition
import androidx.ui.core.dp
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.ipx
import androidx.ui.core.round
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.Padding
import androidx.ui.layout.Wrap
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.ref
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade

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
    items: List<T>,
    onSelected: (T) -> Unit,
    onCancel: (() -> Unit)? = null,
    item: @Composable() (T) -> Unit,
    child: @Composable() (showPopup: () -> Unit) -> Unit
) = composable("PopupMenuTrigger") {
    val navigator = +inject<Navigator>()

    Wrap {
        val coordinatesHolder = +ref<LayoutCoordinates?> { null }
        OnPositioned { coordinatesHolder.value = it }

        val showPopup = {
            val coordinates = coordinatesHolder.value!!

            val width = coordinates.size.width.round()
            val height = coordinates.size.height.round()
            val globalPosition = coordinates.localToRoot(PxPosition.Origin)
            val left = globalPosition.x.round()
            val top = globalPosition.y.round()
            val right = left + width
            val bottom = top + height
            val centerX = left + width / 2
            val centerY = top + height / 2

            val position = when (alignment) {
                Alignment.TopLeft -> IntPxPosition(left, top)
                Alignment.TopCenter -> IntPxPosition(centerX, top)
                Alignment.TopRight -> IntPxPosition(right, top)
                Alignment.CenterLeft -> IntPxPosition(left, centerY)
                Alignment.Center -> IntPxPosition(centerX, centerY)
                Alignment.CenterRight -> IntPxPosition(right, centerY)
                Alignment.BottomLeft -> IntPxPosition(left, bottom)
                Alignment.BottomCenter -> IntPxPosition(centerX, bottom)
                Alignment.BottomRight -> IntPxPosition(right, bottom)
            }

            navigator.push(
                popupMenuRoute(
                    onCancel = onCancel,
                    position = position,
                    items = items,
                    item = item,
                    onSelected = onSelected
                )
            )
        }

        child(showPopup)
    }
}

fun <T> popupMenuRoute(
    alignment: Alignment = Alignment.TopLeft,
    view: View,
    items: List<T>,
    onSelected: (T) -> Unit,
    onCancel: (() -> Unit)? = null,
    item: @Composable() (T) -> Unit
): ControllerRoute {
    val width = view.width.ipx
    val height = view.width.ipx
    val location = intArrayOf(0, 0)
    view.getLocationInWindow(location)
    val left = location[0].ipx
    val top = location[1].ipx
    val right = left + width
    val bottom = top + height
    val centerX = left + width / 2
    val centerY = top + height / 2

    val position = when (alignment) {
        Alignment.TopLeft -> IntPxPosition(left, top)
        Alignment.TopCenter -> IntPxPosition(centerX, top)
        Alignment.TopRight -> IntPxPosition(right, top)
        Alignment.CenterLeft -> IntPxPosition(left, centerY)
        Alignment.Center -> IntPxPosition(centerX, centerY)
        Alignment.CenterRight -> IntPxPosition(right, centerY)
        Alignment.BottomLeft -> IntPxPosition(left, bottom)
        Alignment.BottomCenter -> IntPxPosition(centerX, bottom)
        Alignment.BottomRight -> IntPxPosition(right, bottom)
    }

    return popupMenuRoute(
        position = position,
        items = items,
        onSelected = onSelected,
        onCancel = onCancel,
        item = item
    )
}

fun <T> popupMenuRoute(
    position: IntPxPosition,
    items: List<T>,
    onSelected: (T) -> Unit,
    onCancel: (() -> Unit)? = null,
    item: @Composable() (T) -> Unit
) = composeControllerRoute(
    popOnConfigurationChange = true,
    options = controllerRouteOptions().fade(removesFromViewOnPush = false)
) {
    val navigator = +inject<Navigator>()

    val dismissedHolder = +ref { false }

    val dismiss: (Boolean) -> Unit = { cancelled ->
        if (!dismissedHolder.value) {
            dismissedHolder.value = true
            navigator.pop()
            if (cancelled) onCancel?.invoke()
        }
    }

    PressGestureDetector(
        onPress = { dismiss(true) }
    ) {
        PopupMenuLayout(position) {
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

@Composable
private fun PopupMenuLayout(
    position: IntPxPosition,
    child: @Composable() () -> Unit
) = composable("PopupMenuLayout") {
    Layout(children = child) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = IntPx.Zero,
            minHeight = IntPx.Zero
        )

        val measurable = measureables.first()
        val placeable = measurable.measure(childConstraints)

        val x = if (position.x < constraints.maxWidth / 2) {
            position.x
        } else {
            position.x - placeable.width
        }

        val y = if (position.y < constraints.maxHeight / 2) {
            position.y
        } else {
            position.y - placeable.height
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(x = x, y = y)
        }
    }
}