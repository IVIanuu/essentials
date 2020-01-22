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
import androidx.compose.onActive
import androidx.compose.state
import androidx.ui.core.Opacity
import androidx.ui.input.KeyboardType
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.common.asTextComposable
import com.ivianuu.essentials.ui.core.KeyboardManagerAmbient
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.TextField
import com.ivianuu.essentials.ui.core.current
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

fun TextInputRoute(
    initial: String = "",
    hint: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    title: String? = null,
    allowEmpty: Boolean = true
) = DialogRoute {
    val navigator = NavigatorAmbient.current

    val (currentValue, setCurrentValue) = state { initial }

    TextInputDialog(
        value = currentValue,
        onValueChange = setCurrentValue,
        hint = hint,
        keyboardType = keyboardType,
        title = title?.asTextComposable(),
        positiveButton = {
            DialogButton(
                text = stringResource(R.string.es_ok),
                dismissDialogOnClick = false,
                onClick = if (allowEmpty || currentValue.isNotEmpty()) {
                    {
                        navigator.popTop(result = currentValue)
                    }
                } else {
                    null
                }
            )
        },
        negativeButton = {
            DialogCloseButton(stringResource(R.string.es_cancel))
        }
    )
}

@Composable
fun TextInputDialog(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    positiveButton: @Composable (() -> Unit)? = null,
    negativeButton: @Composable (() -> Unit)? = null,
    neutralButton: @Composable (() -> Unit)? = null
) {
    MaterialDialog(
        buttonLayout = buttonLayout,
        icon = icon,
        title = title,
        content = {
            if (value.isEmpty() && hint != null) {
                Opacity(0.5f) {
                    Text(
                        text = hint,
                        style = MaterialTheme.typography().subtitle1
                    )
                }
            }
            TextField(
                value = value,
                onValueChange = onValueChange,
                focusIdentifier = TextInputDialogInputId,
                keyboardType = keyboardType,
                textStyle = MaterialTheme.typography().subtitle1
            )

            val keyboardManager = KeyboardManagerAmbient.current
            onActive {
                keyboardManager.showKeyboard(TextInputDialogInputId)
                onDispose {
                    keyboardManager.hideKeyboard()
                }
            }
        },
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        neutralButton = neutralButton
    )
}

private const val TextInputDialogInputId = "TextInputDialogInputFieldId"
