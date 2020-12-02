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
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.getAction
import com.ivianuu.essentials.gestures.action.getActionSettingsKey
import com.ivianuu.essentials.gestures.action.getAllActions
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerAction.*
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.ActionItem
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.PickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.SpecialOption
import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.popTopKeyWithResult
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@UiStateBinding
fun actionPickerState(
    scope: CoroutineScope,
    initial: @Initial ActionPickerState = ActionPickerState(),
    actions: Actions<ActionPickerAction>,
    dispatchNavigationAction: DispatchAction<NavigationAction>,
    getActionPickerItems: getActionPickerItems,
    getAction: getAction,
    popTopKeyWithResult: popTopKeyWithResult<ActionPickerResult>,
    requestPermissions: requestPermissions,
) = scope.state(initial) {
    reduceResource({ getActionPickerItems() }) { copy(items = it) }

    actions
        .filterIsInstance<OpenActionSettings>()
        .onEach { action ->
            dispatchNavigationAction(
                NavigationAction.Push(action.item.settingsKey!!)
            )
        }
        .launchIn(this)

    actions
        .filterIsInstance<PickAction>()
        .onEach { action ->
            val result = action.item.getResult() ?: return@onEach
            if (result is ActionPickerResult.Action) {
                val pickedAction = getAction(result.actionKey)
                if (!requestPermissions(pickedAction.permissions)) return@onEach
            }

            popTopKeyWithResult(result)
        }
        .launchIn(this)
}

@FunBinding
suspend fun getActionPickerItems(
    actionPickerDelegates: Set<ActionPickerDelegate>,
    getAllActions: getAllActions,
    getActionSettingsKey: getActionSettingsKey,
    key: ActionPickerKey,
    stringResource: stringResource,
) = buildList<ActionPickerItem> {
    val specialOptions = mutableListOf<SpecialOption>()

    if (key.showDefaultOption) {
        specialOptions += SpecialOption(
            title = stringResource(R.string.es_default),
            getResult = { ActionPickerResult.Default }
        )
    }

    if (key.showNoneOption) {
        specialOptions += SpecialOption(
            title = stringResource(R.string.es_none),
            getResult = { ActionPickerResult.None }
        )
    }

    val actionsAndDelegates = (
            (actionPickerDelegates
                .map { PickerDelegate(it) }) + (getAllActions()
                .map { ActionItem(it, getActionSettingsKey(it.key)) })
            )
        .sortedBy { it.title }

    return specialOptions + actionsAndDelegates
}
