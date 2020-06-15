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
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.tapGestureFilter
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.wrapContentSize
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.common.onBackPressed

@Composable
fun DialogWrapper(
    dismissible: Boolean = true,
    onDismiss: @Composable () -> Unit,
    dialog: @Composable () -> Unit
) {
    if (!dismissible) {
        onBackPressed { }
    }

    var dismissed by state { false }
    var dismissCalled by state { false }
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
            .drawBackground(Color.Black.copy(alpha = 0.6f)),
        gravity = ContentGravity.Center
    ) {
        SafeArea {
            Box(
                modifier = Modifier.tapGestureFilter(onTap = {})
                    .wrapContentSize(align = Alignment.Center),
                gravity = ContentGravity.Center,
                children = dialog
            )
        }
    }
}
