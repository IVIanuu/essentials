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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.ui.common.OnBackPressed
import com.ivianuu.essentials.ui.core.InsetsPadding
import com.ivianuu.essentials.ui.core.rememberState

@Composable
fun DialogWrapper(
    dismissible: Boolean = true,
    onDismiss: @Composable () -> Unit,
    dialog: @Composable () -> Unit
) {
    if (!dismissible) {
        OnBackPressed { }
    }

    var dismissed by rememberState { false }
    var dismissCalled by rememberState { false }
    if (dismissed) {
        if (!dismissCalled) {
            dismissCalled = true
            onDismiss()
        }
    }

    Box(
        modifier = Modifier.tapGestureFilter(
            onTap = {
                if (dismissible) {
                    dismissed = true
                }
            })
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        alignment = Alignment.Center
    ) {
        InsetsPadding {
            Box(
                modifier = Modifier.tapGestureFilter(onTap = {})
                    .wrapContentSize(align = Alignment.Center),
                alignment = Alignment.Center
            ) {
                dialog()
            }
        }
    }
}
