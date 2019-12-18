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

package com.ivianuu.essentials.ui.compose.dialog

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.PxPosition
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.foundation.ColoredRect
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import com.ivianuu.essentials.ui.compose.common.SafeArea
import com.ivianuu.essentials.ui.compose.common.onBackPressed
import com.ivianuu.essentials.ui.compose.common.ref
import com.ivianuu.essentials.ui.compose.navigation.FadeRouteTransition
import com.ivianuu.essentials.ui.compose.navigation.Route
import com.ivianuu.essentials.ui.compose.navigation.navigator

fun DialogRoute(
    dismissible: Boolean = true,
    dismissHandler: @Composable() () -> /*// todo use Unit */ Any? = { navigator.pop() },
    dialog: @Composable() () -> Unit
) = Route(
    opaque = true,
    enterTransition = FadeRouteTransition(),
    exitTransition = FadeRouteTransition()
) {
    DialogWrapper(
        dismissible = dismissible,
        dismissHandler = dismissHandler,
        dialog = dialog
    )
}

@Composable
fun DialogWrapper(
    dismissible: Boolean = true,
    dismissHandler: @Composable() () -> /*// todo use Unit */ Any?,
    dialog: @Composable() () -> Unit
) {
    if (!dismissible) {
        onBackPressed { }
    }

    val dismissed = state { false }
    val dismissCalled = ref { false }
    if (dismissed.value) {
        if (!dismissCalled.value) {
            dismissCalled.value = true
            dismissHandler()
        }
    }

    PressGestureDetector(
        onPress = if (dismissible) {
            { _: PxPosition -> dismissed.value = true }
        } else null
    ) {
        DialogScrim()
        Center {
            SafeArea {
                dialog()
            }
        }
    }
}

@Composable
private fun DialogScrim() {
    ColoredRect(Color.Black.copy(alpha = 0.6f))
}
