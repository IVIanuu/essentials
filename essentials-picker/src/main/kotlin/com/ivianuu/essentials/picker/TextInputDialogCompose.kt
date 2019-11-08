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

package com.ivianuu.essentials.picker

import androidx.compose.Composable
import androidx.compose.onActive
import androidx.compose.unaryPlus
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.layout.Stack
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.common.hideKeyboard
import com.ivianuu.essentials.ui.compose.common.showKeyboard
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.material.dialog.AlertDialog
import com.ivianuu.essentials.ui.compose.material.dialog.AlertDialogButtonLayout

@Composable
fun TextInputDialog(
    value: String,
    hint: String,
    onValueChange: (String) -> Unit,
    dismissOnOutsideTouch: Boolean = true,
    dismissOnBackClick: Boolean = true,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: @Composable() (() -> Unit)? = null,
    title: (@Composable() () -> Unit)? = null,
    positiveButton: (@Composable() () -> Unit)? = null,
    negativeButton: (@Composable() () -> Unit)? = null,
    neutralButton: (@Composable() () -> Unit)? = null
) = composable("TextInputDialog") {
    val showKeyboard = +showKeyboard(TextInputDialogInputId)
    val hideKeyboard = +hideKeyboard()

    AlertDialog(
        dismissOnOutsideTouch = dismissOnOutsideTouch,
        dismissOnBackClick = dismissOnBackClick,
        buttonLayout = buttonLayout,
        icon = icon,
        title = title,
        content = {
            Stack {
                if (value.isEmpty()) {
                    Opacity(0.5f) {
                        Text(
                            text = hint,
                            style = +themeTextStyle { subtitle1 })
                    }
                }
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    focusIdentifier = TextInputDialogInputId,
                    textStyle = +themeTextStyle { subtitle1 }
                )
            }

            +onActive {
                showKeyboard()
                onDispose {
                    hideKeyboard()
                }
            }
        },
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        neutralButton = neutralButton
    )
}

private const val TextInputDialogInputId = "TextInputDialogInputFieldId"