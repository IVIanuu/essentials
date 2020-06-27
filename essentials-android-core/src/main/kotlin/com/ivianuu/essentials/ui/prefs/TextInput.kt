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
import androidx.ui.material.TextButton
import com.ivianuu.essentials.R
import com.ivianuu.essentials.store.DataStore
import com.ivianuu.essentials.ui.datastore.asState
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.TextInputDialog

@Composable
fun TextInputDialogListItem(
    dataStore: DataStore<String>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    dialogHint: String? = null,
    dialogKeyboardType: KeyboardType = KeyboardType.Text,
    allowEmpty: Boolean = true,
    modifier: Modifier = Modifier
) {
    val state = dataStore.asState()
    TextInputDialogListItem(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        trailing = trailing,
        dialogTitle = dialogTitle,
        dialogHint = dialogHint,
        dialogKeyboardType = dialogKeyboardType,
        allowEmpty = allowEmpty
    )
}

@Composable
fun TextInputDialogListItem(
    value: String,
    onValueChange: (String) -> Unit,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    dialogHint: String? = null,
    dialogKeyboardType: KeyboardType = KeyboardType.Text,
    allowEmpty: Boolean = true,
    modifier: Modifier = Modifier
) {
    DialogListItem(
        modifier = modifier,
        title = title?.let { { title() } },
        subtitle = subtitle?.let { { subtitle() } },
        leading = leading?.let { { leading() } },
        trailing = trailing?.let { { trailing() } },
        dialog = { dismiss ->
            val currentValue = stateFor(value) { value }

            TextInputDialog(
                value = currentValue.value,
                onValueChange = { currentValue.value = it },
                title = dialogTitle,
                hint = dialogHint,
                keyboardType = dialogKeyboardType,
                positiveButton = {
                    TextButton(
                        enabled = allowEmpty || currentValue.value.isNotEmpty(),
                        onClick = {
                            onValueChange(currentValue.value)
                            dismiss()
                        }
                    ) { Text(R.string.es_ok) }
                },
                negativeButton = {
                    TextButton(onClick = dismiss) {
                        Text(R.string.es_cancel)
                    }
                }
            )
        }
    )
}
