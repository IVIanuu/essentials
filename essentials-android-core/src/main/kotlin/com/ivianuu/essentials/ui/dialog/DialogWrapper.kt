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
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.tapGestureFilter
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.common.holder
import com.ivianuu.essentials.ui.common.onBackPressed

@Composable
fun DialogWrapper(
    dismissible: Boolean = true,
    dismissHandler: @Composable () -> Unit,
    dialog: @Composable () -> Unit
) {
    if (!dismissible) {
        onBackPressed { }
    }

    val dismissed = state { false }
    val dismissCalled = holder { false }
    if (dismissed.value) {
        if (!dismissCalled.value) {
            dismissCalled.value = true
            dismissHandler()
        }
    }

    Stack(
        modifier = Modifier.tapGestureFilter(
            onTap = {
                if (dismissible) {
                    dismissed.value = true
                }
            })
            .fillMaxSize()
            .drawBackground(Color.Black.copy(alpha = 0.6f))
    ) {
        SafeArea(
            modifier = Modifier.tapGestureFilter(onTap = {})
                .gravity(align = Alignment.Center),
            children = dialog
        )
    }
}
