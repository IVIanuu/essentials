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

package com.ivianuu.essentials.ui.compose.material.dialog

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.material.RadioButton
import com.ivianuu.essentials.ui.compose.common.BlockChildTouches
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.dismissDialog

@Composable
fun <T> SingleChoiceListDialog(
    items: List<T>,
    selectedItem: T,
    onSelect: ((T) -> Unit)? = null,
    item: @Composable() (T) -> Unit,
    dismissOnOutsideTouch: Boolean = true,
    dismissOnBackClick: Boolean = true,
    dismissOnSelect: Boolean = true,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: @Composable() (() -> Unit)? = null,
    title: (@Composable() () -> Unit)? = null,
    positiveButton: (@Composable() () -> Unit)? = null,
    negativeButton: (@Composable() () -> Unit)? = null,
    neutralButton: (@Composable() () -> Unit)? = null
) = composable("SingleChoiceListDialog") {
    val dismissDialog = +dismissDialog()
    ListDialog(
        dismissOnOutsideTouch = dismissOnOutsideTouch,
        dismissOnBackClick = dismissOnBackClick,
        icon = icon,
        title = title,
        buttonLayout = buttonLayout,
        listContent = {
            items.forEachIndexed { index, item ->
                composable(index) {
                    SingleChoiceDialogListItem(
                        title = { item(item) },
                        selected = item == selectedItem,
                        onSelect = onSelect?.let {
                            {
                                onSelect(item)
                                //dismissDialog()
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
private fun SingleChoiceDialogListItem(
    selected: Boolean,
    onSelect: (() -> Unit)? = null,
    title: @Composable() () -> Unit
) = composable("SingleChoiceDialogListItem") {
    SimpleDialogListItem(
        leading = {
            BlockChildTouches {
                RadioButton(
                    selected = selected,
                    onSelect = {}
                )
            }
        },
        title = title,
        onClick = onSelect
    )
}