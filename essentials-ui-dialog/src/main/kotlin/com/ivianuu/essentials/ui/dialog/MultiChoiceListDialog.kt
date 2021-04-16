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

import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*

@Composable
fun <T> MultiChoiceListDialog(
    items: List<T>,
    selectedItems: Set<T>,
    onSelectionsChanged: (Set<T>) -> Unit,
    item: @Composable (T) -> Unit,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SIDE_BY_SIDE,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    positiveButton: @Composable (() -> Unit)? = null,
    negativeButton: @Composable (() -> Unit)? = null,
    neutralButton: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Dialog(
        modifier = modifier,
        icon = icon,
        title = title,
        buttonLayout = buttonLayout,
        applyContentPadding = false,
        content = {
            LazyColumn {
                items(items) { item ->
                    MultiChoiceDialogListItem(
                        title = { item(item) },
                        checked = item in selectedItems,
                        onCheckedChange = {
                            val newSelectedItems = selectedItems.toMutableSet()
                            if (it) {
                                newSelectedItems += item
                            } else {
                                newSelectedItems -= item
                            }

                            onSelectionsChanged(newSelectedItems)
                        }
                    )
                }
            }
        },
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        neutralButton = neutralButton
    )
}

@Composable
private fun MultiChoiceDialogListItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: @Composable () -> Unit
) {
    SimpleDialogListItem(
        leading = {
            Checkbox(
                checked = checked,
                onCheckedChange = null
            )
        },
        title = title,
        onClick = {
            onCheckedChange(!checked)
        }
    )
}
