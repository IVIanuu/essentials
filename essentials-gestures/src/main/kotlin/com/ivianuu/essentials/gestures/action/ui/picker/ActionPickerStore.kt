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
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerAction.PickAction
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.ActionItem
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.PickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.SpecialOption
import com.ivianuu.essentials.store.iterator
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.store.UiStoreBinding
import com.ivianuu.essentials.ui.store.execute
import com.ivianuu.essentials.util.exhaustive
import kotlinx.coroutines.CoroutineScope

@UiStoreBinding
fun CoroutineScope.ActionPickerStore(
    navigator: Navigator,
    getAction: getAction,
    getAllActions: getAllActions,
    actionPickerDelegates: Set<ActionPickerDelegate>,
    requestPermissions: requestPermissions,
    stringResource: stringResource,
    params: ActionPickerParams
) = store<ActionPickerState, ActionPickerAction>(ActionPickerState()) {
    execute(
        block = {
            val specialOptions = mutableListOf<SpecialOption>()

            if (params.showDefaultOption) {
                specialOptions += SpecialOption(
                    title = stringResource(R.string.es_default),
                    getResult = { ActionPickerResult.Default }
                )
            }

            if (params.showNoneOption) {
                specialOptions += SpecialOption(
                    title = stringResource(R.string.es_none),
                    getResult = { ActionPickerResult.None }
                )
            }

            val actionsAndDelegates = (
                    (
                            actionPickerDelegates
                                .map {
                                    PickerDelegate(
                                        it,
                                        navigator
                                    )
                                }
                            ) + (getAllActions().map { ActionItem(it) })
                    )
                .sortedBy { it.title }

            return@execute specialOptions + actionsAndDelegates
        },
        reducer = { copy(items = it) }
    )

    for (action in this) {
        when (action) {
            is PickAction -> {
                val result = action.item.getResult() ?: continue
                if (result is ActionPickerResult.Action) {
                    val pickedAction = getAction(result.actionKey)
                    if (!requestPermissions(pickedAction.permissions)) continue
                }

                navigator.popTop(result = result)
            }
        }.exhaustive
    }
}
