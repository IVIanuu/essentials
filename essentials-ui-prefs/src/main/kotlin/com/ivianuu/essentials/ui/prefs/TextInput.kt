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

package com.ivianuu.essentials.ui.prefs

import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.android.asState
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.rememberState
import com.ivianuu.essentials.ui.dialog.TextInputDialog

@Composable
fun TextInputDialogListItem(
    dataStore: DataStore<String>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    dialogLabel: @Composable (() -> Unit)? = null,
    dialogKeyboardType: KeyboardType = KeyboardType.Text,
    allowEmpty: Boolean = true,
    modifier: Modifier = Modifier
) {
    var state by dataStore.asState()
    TextInputDialogListItem(
        value = state,
        onValueChange = { state = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        trailing = trailing,
        dialogTitle = dialogTitle,
        dialogLabel = dialogLabel,
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
    dialogLabel: @Composable (() -> Unit)? = null,
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
            var currentValue by rememberState(value) { value }

            TextInputDialog(
                value = currentValue,
                onValueChange = { currentValue = it },
                title = dialogTitle,
                label = dialogLabel,
                keyboardType = dialogKeyboardType,
                positiveButton = {
                    TextButton(
                        enabled = allowEmpty || currentValue.isNotEmpty(),
                        onClick = {
                            onValueChange(currentValue)
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
