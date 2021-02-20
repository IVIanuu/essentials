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

import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionRepository
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerAction.*
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.ActionItem
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.PickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.SpecialOption
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.popWithResult
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@UiStateBinding
@Given
fun actionPickerState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial ActionPickerState = ActionPickerState(),
    @Given actions: Actions<ActionPickerAction>,
    @Given actionRepository: ActionRepository,
    @Given key: ActionPickerKey,
    @Given navigator: DispatchAction<NavigationAction>,
    @Given permissionRequester: PermissionRequester,
    @Given resourceProvider: ResourceProvider
): StateFlow<ActionPickerState> = scope.state(initial) {
    reduceResource({ getActionPickerItems(actionRepository, key, resourceProvider) }) {
        copy(items = it)
    }

    actions
        .filterIsInstance<OpenActionSettings>()
        .onEach { action ->
            navigator(Push(action.item.settingsKey!!))
        }
        .launchIn(this)

    actions
        .filterIsInstance<PickAction>()
        .onEach { action ->
            val result = action.item.getResult() ?: return@onEach
            if (result is ActionPickerResult.Action) {
                val pickedAction = actionRepository.getAction(result.actionKey)
                if (!permissionRequester(pickedAction.permissions)) return@onEach
            }

            navigator.popWithResult(result)
        }
        .launchIn(this)
}

private suspend fun getActionPickerItems(
    actionRepository: ActionRepository,
    key: ActionPickerKey,
    resourceProvider: ResourceProvider
): List<ActionPickerItem> = buildList<ActionPickerItem> {
    val specialOptions = mutableListOf<SpecialOption>()

    if (key.showDefaultOption) {
        specialOptions += SpecialOption(
            title = resourceProvider.string(R.string.es_default),
            getResult = { ActionPickerResult.Default }
        )
    }

    if (key.showNoneOption) {
        specialOptions += SpecialOption(
            title = resourceProvider.string(R.string.es_none),
            getResult = { ActionPickerResult.None }
        )
    }

    val actionsAndDelegates = (
            (actionRepository.getActionPickerDelegates()
                .map { PickerDelegate(it) }) + (actionRepository.getAllActions()
                .map { ActionItem(it, actionRepository.getActionSettingsKey(it.id)) })
            )
        .sortedBy { it.title }

    return specialOptions + actionsAndDelegates
}
