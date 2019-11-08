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
import androidx.ui.core.Text
import androidx.ui.material.RadioButton
import com.ivianuu.essentials.ui.compose.common.BlockChildTouches
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.dismissDialog

@Composable
fun <T> SingleChoiceListDialog(
    items: List<Pair<T, String>>,
    selectedItem: T,
    onSelect: ((T) -> Unit)? = null,
    dismissOnOutsideTouch: Boolean = true,
    dismissOnBackClick: Boolean = true,
    icon: @Composable() (() -> Unit)? = null,
    title: (@Composable() () -> Unit)? = null,
    buttons: (@Composable() () -> Unit)? = null
) = composable("SingleChoiceListDialog") {
    val dismissDialog = +dismissDialog()
    ListDialog(
        dismissOnOutsideTouch = dismissOnOutsideTouch,
        dismissOnBackClick = dismissOnBackClick,
        icon = icon,
        title = title,
        buttons = buttons,
        listContent = {
            items.forEachIndexed { index, (item, title) ->
                composable(index) {
                    SingleChoiceDialogListItem(
                        title = title,
                        selected = item == selectedItem,
                        onSelect = onSelect?.let {
                            {
                                onSelect(item)
                                dismissDialog()
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun SingleChoiceDialogListItem(
    title: String,
    selected: Boolean,
    onSelect: (() -> Unit)? = null
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
        title = { Text(title) },
        onClick = onSelect
    )
}