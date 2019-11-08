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
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.PxPosition
import androidx.ui.core.dp
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Center
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.Padding
import androidx.ui.material.surface.Card
import com.ivianuu.essentials.ui.compose.common.onBackPressed
import com.ivianuu.essentials.ui.compose.core.composable

// todo callbacks like onShow, onDismiss, onCancel etc
// todo add DialogState + ambient to control dialog state from descendents

@Composable
fun Dialog(
    dismissOnOutsideTouch: Boolean = true,
    dismissOnBackClick: Boolean = true,
    body: @Composable() () -> Unit
) = composable("Dialog") {
    val dismissDialog = +ambient(DismissDialogAmbient)

    if (dismissOnBackClick) {
        composable("back clicks") {
            +onBackPressed(callback = dismissDialog)
        }
    }

    PressGestureDetector(
        onPress = if (dismissOnOutsideTouch) {
            { _: PxPosition -> dismissDialog() }
        } else null
    ) {
        Center {
            Padding(
                left = 32.dp,
                top = 32.dp,
                right = 32.dp,
                bottom = 32.dp
            ) {
                PressGestureDetector {
                    ConstrainedBox(
                        constraints = DpConstraints(
                            minWidth = 280.dp,
                            maxWidth = 356.dp
                        )
                    ) {
                        Card(
                            shape = RoundedCornerShape(size = 4.dp),
                            elevation = 24.dp
                        ) {
                            body()
                        }
                    }
                }
            }
        }
        body()
    }
}
