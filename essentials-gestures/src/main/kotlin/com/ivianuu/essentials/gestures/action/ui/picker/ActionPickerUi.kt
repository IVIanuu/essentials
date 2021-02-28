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

import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerAction.OpenActionSettings
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerAction.PickAction
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.injekt.Given

@KeyUiBinding<ActionPickerKey>
@Given
fun actionPickerUi(
    @Given stateProvider: @Composable () -> @UiState ActionPickerState,
    @Given dispatch: DispatchAction<ActionPickerAction>,
): KeyUi = {
    val state = stateProvider()
    Scaffold(
        topBar = { TopAppBar(title = { Text(R.string.es_action_picker_title) }) }
    ) {
        ResourceLazyColumnFor(state.items) { item ->
            ActionPickerItem(
                item = item,
                onClick = { dispatch(PickAction(item)) },
                onOpenSettingsClick = { dispatch(OpenActionSettings(item)) }
            )
        }
    }
}

@Composable
private fun ActionPickerItem(
    onClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    item: ActionPickerItem,
) {
    ListItem(
        leading = { item.icon(Modifier.size(24.dp)) },
        trailing = if (item.settingsKey != null) ({
            IconButton(onClick = onOpenSettingsClick) {
                Icon(R.drawable.es_ic_settings, null)
            }
        }) else null,
        title = { Text(item.title) },
        onClick = onClick
    )
}
