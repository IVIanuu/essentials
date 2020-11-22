/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.rememberState
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.navigation.popTop

fun TextInputRoute(
    initial: String = "",
    label: @Composable () -> Unit,
    keyboardOptions: KeyboardOptions,
    title: @Composable (() -> Unit)? = null,
    allowEmpty: Boolean = true
) = DialogRoute {
    val navigator = NavigatorAmbient.current

    var currentValue by rememberState { initial }

    TextInputDialog(
        value = currentValue,
        onValueChange = { currentValue = it },
        label = label,
        keyboardOptions = keyboardOptions,
        title = title,
        positiveButton = {
            TextButton(
                enabled = allowEmpty || currentValue.isNotEmpty(),
                onClick = { navigator.popTop(result = currentValue) }
            ) { Text(R.string.es_ok) }
        },
        negativeButton = {
            TextButton(
                onClick = { navigator.popTop() }
            ) { Text(R.string.es_cancel) }
        }
    )
}

@Composable
fun TextInputDialog(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    positiveButton: @Composable (() -> Unit)? = null,
    negativeButton: @Composable (() -> Unit)? = null,
    neutralButton: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Dialog(
        modifier = modifier,
        buttonLayout = buttonLayout,
        icon = icon,
        title = title,
        content = {
            Box {
                val focusRequester = remember { FocusRequester() }
                TextField(
                    modifier = Modifier
                        .focusRequester(focusRequester),
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = keyboardOptions,
                    textStyle = MaterialTheme.typography.subtitle1,
                    label = label ?: {}
                )

                onActive { focusRequester.requestFocus() }
            }
        },
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        neutralButton = neutralButton
    )
}
