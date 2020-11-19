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

package com.ivianuu.essentials.gestures.action.ui.picker

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.injekt.FunBinding

@FunBinding
@Composable
fun ActionPickerPage(
    dispatch: DispatchAction<ActionPickerAction>,
    state: @UiState ActionPickerState
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(R.string.es_action_picker_title) }) }
    ) {
        ResourceLazyColumnFor(state.items) { item ->
            ActionPickerItem(
                item = item,
                onClick = { dispatch(ActionPickerAction.PickAction(item)) }
            )
        }
    }
}

@Composable
private fun ActionPickerItem(
    onClick: () -> Unit,
    item: ActionPickerItem
) {
    ListItem(
        leading = { item.icon() },
        title = { Text(item.title) },
        onClick = onClick
    )
}
