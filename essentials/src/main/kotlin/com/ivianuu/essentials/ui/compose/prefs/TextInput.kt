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

package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.ui.input.KeyboardType
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.core.state
import com.ivianuu.essentials.ui.compose.dialog.DialogButton
import com.ivianuu.essentials.ui.compose.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.compose.dialog.TextInputDialog
import com.ivianuu.essentials.ui.compose.resources.stringResource
import com.ivianuu.kprefs.Pref

@Composable
fun TextInputPreference(
    pref: Pref<String>,
    dialogHint: String? = null,
    dialogKeyboardType: KeyboardType = KeyboardType.Text,
    allowEmpty: Boolean = true,
    title: @Composable() () -> Unit,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((String) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    dialogTitle: @Composable() (() -> Unit)? = title
) = composableWithKey("TextInputPreference:${pref.key}") {
    DialogPreference(
        pref = pref,
        dialog = { dismiss ->
            val (currentValue, setCurrentValue) = state { pref.get() }

            TextInputDialog(
                value = currentValue,
                onValueChange = setCurrentValue,
                title = dialogTitle,
                hint = dialogHint,
                keyboardType = dialogKeyboardType,
                positiveButton = {
                    DialogButton(
                        text = stringResource(R.string.es_ok),
                        onClick = if (allowEmpty || currentValue.isNotEmpty()) {
                            {
                                if (onChange?.invoke(currentValue) != false) {
                                    pref.set(currentValue)
                                }
                            }
                        } else {
                            null
                        }
                    )
                },
                negativeButton = { DialogCloseButton(stringResource(R.string.es_cancel)) }
            )
        },
        title = title,
        summary = summary,
        leading = leading,
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies
    )
}