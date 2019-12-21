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

package com.ivianuu.essentials.ui.dialog

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.PxPosition
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.layout.Center
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.common.onBackPressed
import com.ivianuu.essentials.ui.common.ref

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
        SafeArea {
            Center {
                PressGestureDetector(children = dialog)
            }
        }
    }
}