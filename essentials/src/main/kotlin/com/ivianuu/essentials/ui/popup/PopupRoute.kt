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

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.ConfigurationAmbient
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.unit.IntPxPosition
import androidx.ui.unit.ipx
import com.ivianuu.essentials.ui.common.holder
import com.ivianuu.essentials.ui.layout.NonNullSingleChildLayout
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.transition.FadeRouteTransition

fun PopupRoute(
    position: IntPxPosition,
    onCancel: (() -> Unit)? = null,
    popup: @Composable () -> Unit
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
    child: @Composable () -> Unit
) {
    NonNullSingleChildLayout(child = child) { measureable, constraints ->
        val childConstraints = constraints.copy(
            minWidth = 0.ipx,
            minHeight = 0.ipx
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
