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

package com.ivianuu.essentials.ui.popup

import android.view.View
import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.ConfigurationAmbient
import androidx.ui.core.IntPx
import androidx.ui.core.IntPxPosition
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.ipx
import com.ivianuu.essentials.ui.common.holder
import com.ivianuu.essentials.ui.core.current
import com.ivianuu.essentials.ui.layout.NonNullSingleChildLayout
import com.ivianuu.essentials.ui.navigation.FadeRouteTransition
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route

fun PopupRoute(
    alignment: Alignment = Alignment.BottomLeft,
    view: View,
    onCancel: (() -> Unit)? = null,
    popup: @Composable() () -> Unit
): Route {
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

    return PopupRoute(
        position = position,
        onCancel = onCancel,
        popup = popup
    )
}

fun PopupRoute(
    position: IntPxPosition,
    onCancel: (() -> Unit)? = null,
    popup: @Composable() () -> Unit
) = Route(
    opaque = true,
    enterTransition = FadeRouteTransition(),
    exitTransition = FadeRouteTransition()
) {
    val navigator = NavigatorAmbient.current

    val configuration = ConfigurationAmbient.current
    val initialConfiguration = remember { configuration }
    if (configuration !== initialConfiguration) {
        navigator.popTop()
    }

    val dismissed = holder { false }

    val dismiss: (Boolean) -> Unit = { cancelled ->
        if (!dismissed.value) {
            dismissed.value = true
            navigator.popTop()
            if (cancelled) onCancel?.invoke()
        }
    }

    PressGestureDetector(
        onPress = { dismiss(true) }
    ) {
        PopupLayout(position) {
            PressGestureDetector(children = popup)
        }
    }
}

@Composable
private fun PopupLayout(
    position: IntPxPosition,
    child: @Composable() () -> Unit
) {
    NonNullSingleChildLayout(child = child) { measureable, constraints ->
        val childConstraints = constraints.copy(
            minWidth = IntPx.Zero,
            minHeight = IntPx.Zero
        )

        val placeable = measureable.measure(childConstraints)

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
