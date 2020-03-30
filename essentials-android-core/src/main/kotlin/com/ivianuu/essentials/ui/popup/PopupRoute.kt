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
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.TapGestureDetector
import androidx.ui.foundation.Box
import androidx.ui.unit.IntPx
import androidx.ui.unit.IntPxBounds
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import com.ivianuu.essentials.ui.common.holder
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.transition.FadeRouteTransition

fun PopupRoute(
    position: IntPxBounds,
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


    PopupLayout(
        position = position,
        modifier = TapGestureDetector(
            onTap = { dismiss(true) }
        )
    ) {
        Box(modifier = TapGestureDetector(onTap = {}), children = popup)
    }
}

@Composable
private fun PopupLayout(
    position: IntPxBounds,
    modifier: Modifier,
    children: @Composable () -> Unit
) {
    Layout(children = children, modifier = modifier) { measureables, constraints, _ ->
        val childConstraints = constraints.copy(
            minWidth = 0.ipx,
            minHeight = 0.ipx
        )

        val placeable = measureables.single().measure(childConstraints)

        var y = position.top
        var x: IntPx

        // Find the ideal horizontal position.
        if ((position.left + position.right / 2) < constraints.maxWidth / 2) {
            x = position.left
        } else if (position.left < position.right) {
            x = position.right - placeable.width
        } else {
            x = position.right - placeable.width // todo based on ltr/rtl
        }

        x = x.coerceIn(8.dp.toIntPx(), constraints.maxWidth - placeable.width - 8.dp.toIntPx())
        y = y.coerceIn(8.dp.toIntPx(), constraints.maxHeight - placeable.height - 8.dp.toIntPx())

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(x = x, y = y)
        }
    }
}
