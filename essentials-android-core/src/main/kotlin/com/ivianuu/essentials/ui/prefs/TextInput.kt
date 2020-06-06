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

package com.ivianuu.essentials.ui.prefs

import androidx.compose.Composable
import androidx.compose.stateFor
import androidx.ui.core.Modifier
import androidx.ui.input.KeyboardType
import com.ivianuu.essentials.R
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.box.asState
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.DialogButton
import com.ivianuu.essentials.ui.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.dialog.TextInputDialog

@Composable
fun TextInputPreference(
    box: Box<String>,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    dialogHint: String? = null,
    dialogKeyboardType: KeyboardType = KeyboardType.Text,
    allowEmpty: Boolean = true,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
    TextInputPreference(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        dialogTitle = dialogTitle,
        dialogHint = dialogHint,
        dialogKeyboardType = dialogKeyboardType,
        allowEmpty = allowEmpty
    )
}

@Composable
fun TextInputPreference(
    value: String,
    onValueChange: (String) -> Unit,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    dialogHint: String? = null,
    dialogKeyboardType: KeyboardType = KeyboardType.Text,
    allowEmpty: Boolean = true,
    modifier: Modifier = Modifier
) {
    DialogPreference(
        modifier = modifier,
        title = title?.let { { title() } },
        summary = summary?.let { { summary() } },
        leading = leading?.let { { leading() } },
        dialog = { dismiss ->
            val currentValue = stateFor(value) { value }

            TextInputDialog(
                value = currentValue.value,
                onValueChange = { currentValue.value = it },
                title = dialogTitle,
                hint = dialogHint,
                keyboardType = dialogKeyboardType,
                positiveButton = {
                    DialogButton(
                        enabled = allowEmpty || currentValue.value.isNotEmpty(),
                        onClick = { onValueChange(currentValue.value) }
                    ) { Text(R.string.es_ok) }
                },
                negativeButton = {
                    DialogCloseButton {
                        Text(R.string.es_cancel)
                    }
                }
            )
        }
    )
}
