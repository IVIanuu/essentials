/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.platform.ConfigurationAmbient
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.animatable.animatable
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.common.rememberUntrackedState
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.Route

fun PopupRoute(
    position: Rect,
    onCancel: (() -> Unit)? = null,
    popup: @Composable () -> Unit
) = Route(
    opaque = true,
    enterTransition = FadeStackTransition(),
    exitTransition = FadeStackTransition()
) {
    val navigator = NavigatorAmbient.current

    val configuration = ConfigurationAmbient.current
    val initialConfiguration = remember { configuration }
    if (configuration !== initialConfiguration) {
        navigator.popTop()
    }

    var dismissed by rememberUntrackedState { false }

    val dismiss: (Boolean) -> Unit = { cancelled ->
        if (!dismissed) {
            dismissed = true
            navigator.popTop()
            if (cancelled) onCancel?.invoke()
        }
    }

    PopupLayout(
        position = position,
        modifier = Modifier.tapGestureFilter(
            onTap = { dismiss(true) }
        )
    ) {
        Box(
            modifier = Modifier.tapGestureFilter(onTap = {})
                .animatable(PopupTag)
        ) {
            popup()
        }
    }
}

private val PopupTag = Any()

@Composable
private fun PopupLayout(
    position: Rect,
    modifier: Modifier,
    children: @Composable () -> Unit
) {
    Layout(children = children, modifier = modifier) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = 0,
            minHeight = 0
        )

        val placeable = measureables.single().measure(childConstraints)

        var y = position.top.toInt()
        var x: Int

        // Find the ideal horizontal position.
        if ((position.left + position.right / 2) < constraints.maxWidth / 2) {
            x = position.left.toInt()
        } else if (position.left < position.right) {
            x = (position.right - placeable.width).toInt()
        } else {
            x = (position.right - placeable.width).toInt() // todo based on ltr/rtl
        }

        x = x.coerceIn(8.dp.toIntPx(), constraints.maxWidth - placeable.width - 8.dp.toIntPx())
        y = y.coerceIn(8.dp.toIntPx(), constraints.maxHeight - placeable.height - 8.dp.toIntPx())

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(x = x, y = y)
        }
    }
}