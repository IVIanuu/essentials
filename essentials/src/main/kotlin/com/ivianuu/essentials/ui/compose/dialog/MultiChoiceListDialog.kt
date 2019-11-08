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

package com.ivianuu.essentials.ui.compose.dialog

import androidx.compose.Composable
import androidx.ui.material.Checkbox
import com.ivianuu.essentials.ui.compose.common.BlockChildTouches
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun <T> MultiChoiceListDialog(
    items: List<T>,
    selectedItems: List<T>,
    onSelectionsChanged: ((List<T>) -> Unit)? = null,
    item: @Composable() (T) -> Unit,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: @Composable() (() -> Unit)? = null,
    title: (@Composable() () -> Unit)? = null,
    positiveButton: (@Composable() () -> Unit)? = null,
    negativeButton: (@Composable() () -> Unit)? = null,
    neutralButton: (@Composable() () -> Unit)? = null
) = composable("MultiChoiceListDialog") {
    ListDialog(
        icon = icon,
        title = title,
        buttonLayout = buttonLayout,
        listContent = {
            items.forEachIndexed { index, item ->
                composable(index) {
                    MultiChoiceDialogListItem(
                        title = { item(item) },
                        checked = item in selectedItems,
                        onCheckedChange = onSelectionsChanged?.let {
                            {
                                val newSelectedItems = selectedItems.toMutableList()
                                if (it) {
                                    newSelectedItems += item
                                } else {
                                    newSelectedItems -= item
                                }

                                onSelectionsChanged(newSelectedItems)
                            }
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
    onCheckedChange: ((Boolean) -> Unit)? = null,
    title: @Composable() () -> Unit
) = composable("MultiChoiceDialogListItem") {
    SimpleDialogListItem(
        leading = {
            BlockChildTouches {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {}
                )
            }
        },
        title = title,
        onClick = onCheckedChange?.let {
            { onCheckedChange(!checked) }
        }
    )
}