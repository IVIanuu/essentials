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
import androidx.ui.core.PxPosition
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.foundation.ColoredRect
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import com.ivianuu.essentials.ui.compose.common.SafeArea
import com.ivianuu.essentials.ui.compose.common.onBackPressed
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.invokeAsComposable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade

fun dialogRoute(
    dismissible: Boolean = true,
    dialog: @Composable() () -> Unit
) = composeControllerRoute(
    options = controllerRouteOptions().fade(
        removesFromViewOnPush = false
    )
) {
    val navigator = inject<Navigator>()
    if (!dismissible) {
        onBackPressed { }
    }
    PressGestureDetector(
        onPress = if (dismissible) {
            { _: PxPosition -> navigator.pop() }
        } else null
    ) {
        DialogScrim()
        Center {
            SafeArea {
                dialog.invokeAsComposable()
            }
        }
    }
}

@Composable
private fun DialogScrim() = composable {
    ColoredRect(Color.Black.copy(alpha = 0.6f))
}