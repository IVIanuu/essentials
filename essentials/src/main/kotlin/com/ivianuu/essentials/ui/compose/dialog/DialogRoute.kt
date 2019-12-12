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
import com.ivianuu.essentials.ui.compose.core.ref
import com.ivianuu.essentials.ui.compose.es.ComposeControllerRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.director.ControllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade

fun DialogRoute(
    dismissible: Boolean = true,
    dialog: @Composable() () -> Unit
) = ComposeControllerRoute(
    options = ControllerRouteOptions().fade(
        removesFromViewOnPush = false
    )
) {
    val navigator = inject<Navigator>()
    if (!dismissible) {
        onBackPressed { }
    }

    val dismissed = ref { false }

    PressGestureDetector(
        onPress = if (dismissible) {
            { _: PxPosition ->
                if (!dismissed.value) {
                    dismissed.value = true
                    navigator.pop()
                }
            }
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
