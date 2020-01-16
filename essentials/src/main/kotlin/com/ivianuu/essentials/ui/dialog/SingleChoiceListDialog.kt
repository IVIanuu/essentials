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

package com.ivianuu.essentials.ui.dialog

import androidx.compose.Composable
import androidx.compose.key
import com.ivianuu.essentials.ui.common.AbsorbPointer
import com.ivianuu.essentials.composehelpers.current
import com.ivianuu.essentials.ui.material.RadioButton
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient

@Composable
fun <T> SingleChoiceListDialog(
    items: List<T>,
    selectedItem: T,
    onSelect: ((T) -> Unit)? = null,
    item: @Composable() (T) -> Unit,
    dismissOnSelection: Boolean = true,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: (@Composable() () -> Unit)? = null,
    title: (@Composable() () -> Unit)? = null,
    positiveButton: (@Composable() () -> Unit)? = null,
    negativeButton: (@Composable() () -> Unit)? = null,
    neutralButton: (@Composable() () -> Unit)? = null
) {
    val navigator = NavigatorAmbient.current

    ScrollableDialog(
        icon = icon,
        title = title,
        buttonLayout = buttonLayout,
        listContent = {
            items.forEachIndexed { index, item ->
                key(index) {
                    SingleChoiceDialogListItem(
                        title = { item(item) },
                        selected = item == selectedItem,
                        onSelect = onSelect?.let {
                            {
                                onSelect(item)
                                if (dismissOnSelection) navigator.popTop()
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
) {
    SimpleDialogListItem(
        leading = {
            AbsorbPointer {
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
