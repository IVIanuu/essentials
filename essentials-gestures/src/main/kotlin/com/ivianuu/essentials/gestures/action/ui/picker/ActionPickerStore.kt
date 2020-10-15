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
import com.ivianuu.essentials.gestures.action.getActions
import com.ivianuu.essentials.gestures.action.ui.ActionIcon
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerAction.ItemClicked
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.ActionItem
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.PickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerItem.SpecialOption
import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.store.storeProvider
import com.ivianuu.essentials.ui.navigation.Navigator
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
    getActions: getActions,
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
                            ) + (getActions().map { ActionItem(it) })
                    )
                .sortedBy { it.title }

            return@execute specialOptions + actionsAndDelegates
        },
        reducer = { copy(items = it) }
    )

    onEachAction { action ->
        when (action) {
            is ItemClicked -> {
                val result = action.item.getResult() ?: return@onEachAction
                if (result is ActionPickerResult.Action) {
                    val pickedAction = getAction(result.actionKey)
                    if (!requestPermissions(pickedAction.permissions)) return@onEachAction
                }

                navigator.popTop(result = result)
            }
        }.exhaustive
    }
}

sealed class ActionPickerResult {
    data class Action(val actionKey: String) : ActionPickerResult()
    object Default : ActionPickerResult()
    object None : ActionPickerResult()
}

@Immutable
sealed class ActionPickerItem {
    @Immutable
    data class ActionItem(val action: Action) : ActionPickerItem() {
        override val title: String
            get() = action.title

        @Composable
        override fun icon() {
            ActionIcon(action = action)
        }

        override suspend fun getResult() = ActionPickerResult.Action(action.key)
    }

    @Immutable
    data class PickerDelegate(
        val delegate: ActionPickerDelegate,
        val navigator: Navigator
    ) : ActionPickerItem() {
        override val title: String
            get() = delegate.title

        @Composable
        override fun icon() {
            delegate.icon()
        }

        override suspend fun getResult() = delegate.getResult()
    }

    @Immutable
    data class SpecialOption(
        override val title: String,
        val getResult: () -> ActionPickerResult?
    ) : ActionPickerItem() {

        @Composable
        override fun icon() {
            Spacer(Modifier.size(24.dp))
        }

        override suspend fun getResult() = getResult.invoke()
    }

    abstract val title: String

    @Composable
    abstract fun icon()

    abstract suspend fun getResult(): ActionPickerResult?
}

@Immutable
data class ActionPickerState(val items: Resource<List<ActionPickerItem>> = Idle)

sealed class ActionPickerAction {
    data class ItemClicked(val item: ActionPickerItem) : ActionPickerAction()
}
