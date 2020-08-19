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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.state
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Text
import androidx.compose.material.TextButton
import com.ivianuu.essentials.R
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.datastore.asState
import com.ivianuu.essentials.ui.dialog.MultiChoiceListDialog

@Composable
fun <T> MultiChoiceDialogListItem(
    dataStore: DataStore<Set<T>>,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    items: List<MultiChoiceDialogListItem.Item<T>>,
    modifier: Modifier = Modifier
) {
    val state = dataStore.asState()
    MultiChoiceDialogListItem(
        value = state.value,
        onValueChange = { state.value = it },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        trailing = trailing,
        dialogTitle = dialogTitle,
        items = items
    )
}

@Composable
fun <T> MultiChoiceDialogListItem(
    value: Set<T>,
    onValueChange: (Set<T>) -> Unit,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    items: List<MultiChoiceDialogListItem.Item<T>>,
    modifier: Modifier = Modifier
) {
    DialogListItem(
        modifier = modifier,
        title = title?.let { { title() } },
        subtitle = subtitle?.let { { subtitle() } },
        leading = leading?.let { { leading() } },
        trailing = trailing?.let { { trailing() } },
        dialog = { dismiss ->
            val selectedItems = state {
                value
                    .map { value -> items.first { it.value == value } }
            }

            MultiChoiceListDialog(
                items = items,
                selectedItems = selectedItems.value,
                onSelectionsChanged = { selectedItems.value = it },
                item = { Text(it.title) },
                title = dialogTitle,
                positiveButton = {
                    TextButton(
                        onClick = {
                            val newValue = selectedItems.value.map { it.value }.toSet()
                            onValueChange(newValue)
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

object MultiChoiceDialogListItem {
    @Immutable
    data class Item<T>(
        val title: String,
        val value: T
    )
}
