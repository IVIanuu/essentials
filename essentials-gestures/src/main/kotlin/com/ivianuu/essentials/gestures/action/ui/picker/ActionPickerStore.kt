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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.getAction
import com.ivianuu.essentials.gestures.action.getAllActions
import com.ivianuu.essentials.gestures.action.ui.ActionIcon
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerAction.PickAction
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.ActionItem
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.PickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.SpecialOption
import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.store.storeProvider
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.store.execute
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Binding

@Binding
fun actionPickerStore(
    navigator: Navigator,
    getAction: getAction,
    getAllActions: getAllActions,
    actionPickerDelegates: Set<ActionPickerDelegate>,
    requestPermissions: requestPermissions,
    stringResource: stringResource,
    showDefaultOption: @Assisted Boolean,
    showNoneOption: @Assisted Boolean,
) = storeProvider<ActionPickerState, ActionPickerAction>(ActionPickerState()) {
    execute(
        block = {
            val specialOptions = mutableListOf<SpecialOption>()

            if (showDefaultOption) {
                specialOptions += SpecialOption(
                    title = stringResource(R.string.es_default),
                    getResult = { ActionPickerResult.Default }
                )
            }

            if (showNoneOption) {
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
