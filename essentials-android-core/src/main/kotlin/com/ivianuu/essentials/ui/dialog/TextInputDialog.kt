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

import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.runtime.state
import androidx.compose.ui.FocusModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.navigation.DialogRoute
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

fun TextInputRoute(
    initial: String = "",
    label: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    title: String? = null,
    allowEmpty: Boolean = true
) = DialogRoute {
    val navigator = NavigatorAmbient.current

    val (currentValue, setCurrentValue) = state { initial }

    TextInputDialog(
        value = currentValue,
        onValueChange = setCurrentValue,
        label = label,
        keyboardType = keyboardType,
        title = title?.let {
            {
                Text(it)
            }
        },
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
    label: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
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
            val focusModifier = FocusModifier()

            TextField(
                value = value,
                onValueChange = onValueChange,
                keyboardType = keyboardType,
                textStyle = MaterialTheme.typography.subtitle1,
                modifier = focusModifier,
                label = { if (label != null) Text(label) }
            )

            onActive {
                focusModifier.requestFocus()
                onDispose {
                    focusModifier.freeFocus()
                }
            }
        },
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        neutralButton = neutralButton
    )
}
