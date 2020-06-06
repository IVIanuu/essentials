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
import androidx.compose.Immutable
import androidx.compose.state
import androidx.ui.core.Modifier
import com.ivianuu.essentials.R
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.box.asState
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.DialogButton
import com.ivianuu.essentials.ui.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.dialog.MultiChoiceListDialog

@Composable
fun <T> MultiChoiceListPreference(
    box: Box<Set<T>>,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    items: List<MultiChoiceListPreference.Item<T>>,
    modifier: Modifier = Modifier
) {
    val state = box.asState()
    MultiChoiceListPreference(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        summary = summary,
        leading = leading,
        dialogTitle = dialogTitle,
        items = items
    )
}

@Composable
fun <T> MultiChoiceListPreference(
    value: Set<T>,
    onValueChange: (Set<T>) -> Unit,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    items: List<MultiChoiceListPreference.Item<T>>,
    modifier: Modifier = Modifier
) {
    DialogPreference(
        modifier = modifier,
        title = title?.let { { title() } },
        summary = summary?.let { { summary() } },
        leading = leading?.let { { leading() } },
        dialog = { dismiss ->
            val selectedItems = state {
                value
                    .map { value -> items.first { it.value == value } }
            }

            MultiChoiceListDialog(
                items = items,
                selectedItems = selectedItems.value,
                onSelectionsChanged = { selectedItems.value = it },
                itemCallback = { Text(it.title) },
                title = dialogTitle,
                positiveButton = {
                    DialogButton(
                        onClick = {
                            val newValue = selectedItems.value.map { it.value }.toSet()
                            onValueChange(newValue)
                        }
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

object MultiChoiceListPreference {
    @Immutable
    data class Item<T>(
        val title: String,
        val value: T
    )
}
