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
import androidx.ui.core.Text
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.core.state
import com.ivianuu.essentials.ui.compose.dialog.DialogButton
import com.ivianuu.essentials.ui.compose.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.compose.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.compose.resources.stringResource
import com.ivianuu.kprefs.Pref

@Composable
fun SingleChoiceListPreference(
    pref: Pref<String>,
    items: List<SingleChoiceListPreference.Item>,
    title: @Composable() () -> Unit,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((String) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    dialogTitle: (@Composable() () -> Unit)? = title
) = composableWithKey("SingleChoiceListPreference:${pref.key}") {
    DialogPreference(
        pref = pref,
        title = title,
        summary = summary,
        leading = leading,
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies,
        dialog = { dismiss ->
            val (selectedItem, setSelectedItem) = state {
                items.first { it.value == pref.get() }
            }

            SingleChoiceListDialog(
                items = items,
                selectedItem = selectedItem,
                onSelect = setSelectedItem,
                item = { Text(it.title) },
                title = dialogTitle,
                positiveButton = {
                    DialogButton(
                        text = stringResource(R.string.es_ok),
                        onClick = {
                            val newValue = selectedItem.value
                            if (onChange?.invoke(newValue) != false) {
                                pref.set(newValue)
                            }
                        }
                    )
                },
                negativeButton = { DialogCloseButton(stringResource(R.string.es_cancel)) }
            )
        }
    )
}

object SingleChoiceListPreference {
    data class Item(
        val title: String,
        val value: String
    ) {
        constructor(value: String) : this(value, value)
    }
}