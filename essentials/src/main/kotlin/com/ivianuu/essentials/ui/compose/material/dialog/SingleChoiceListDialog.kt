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
import androidx.ui.material.RadioButton
import com.ivianuu.essentials.ui.compose.common.BlockChildTouches
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun <T> SingleChoiceListDialog(
    dismissOnOutsideTouch: Boolean = true,
    dismissOnBackClick: Boolean = true,
    title: (@Composable() () -> Unit)? = null,
    buttons: (@Composable() () -> Unit)? = null,
    items: List<T>,
    selectedIndex: Int,
    item: @Composable() (Int, Boolean, T) -> Unit
) = composable("SingleChoiceListDialog") {
    ListDialog(
        dismissOnOutsideTouch = dismissOnOutsideTouch,
        dismissOnBackClick = dismissOnBackClick,
        title = title,
        buttons = buttons,
        listContent = {
            items.forEachIndexed { index, item ->
                item(index, index == selectedIndex, item)
            }
        }
    )
}

@Composable
fun SingleChoiceDialogListItem(
    title: @Composable() () -> Unit,
    selected: Boolean,
    onSelect: () -> Unit
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