// injekt-incremental-fix 1615382656101 injekt-end
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionRepository
import com.ivianuu.essentials.gestures.action.ui.ActionIcon
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerAction.OpenActionSettings
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerAction.PickAction
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ActionPickerKey(
    val showDefaultOption: Boolean = false,
    val showNoneOption: Boolean = false,
) : Key<ActionPickerKey.Result> {
    sealed class Result {
        data class Action(val actionKey: String) : Result()
        object Default : Result()
        object None : Result()
    }
}

@Given
val actionPickerKeyModule = KeyModule<ActionPickerKey>()

@Given
fun actionPickerUi(
    @Given stateFlow: StateFlow<ActionPickerState>,
    @Given dispatch: Collector<ActionPickerAction>,
): KeyUi<ActionPickerKey> = {
    val state by stateFlow.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_action_picker_title)) }) }
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
                Icon(painterResource(R.drawable.es_ic_settings), null)
            }
        }) else null,
        title = { Text(item.title) },
        onClick = onClick
    )
}

data class ActionPickerState(val items: Resource<List<ActionPickerItem>> = Idle)

sealed class ActionPickerAction {
    data class OpenActionSettings(val item: ActionPickerItem) : ActionPickerAction()
    data class PickAction(val item: ActionPickerItem) : ActionPickerAction()
}

@Given
fun actionPickerState(
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given initial: @Initial ActionPickerState = ActionPickerState(),
    @Given actions: Flow<ActionPickerAction>,
    @Given actionRepository: ActionRepository,
    @Given key: ActionPickerKey,
    @Given navigator: Collector<NavigationAction>,
    @Given permissionRequester: PermissionRequester,
    @Given resourceProvider: ResourceProvider
): @Scoped<KeyUiGivenScope> StateFlow<ActionPickerState> = scope.state(initial) {
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
            if (result is ActionPickerKey.Result.Action) {
                val pickedAction = actionRepository.getAction(result.actionKey)
                if (!permissionRequester(pickedAction.permissions)) return@onEach
            }

            navigator(Pop(key, result))
        }
        .launchIn(this)
}

@Given
val actionPickerActions: @Scoped<KeyUiGivenScope> MutableSharedFlow<ActionPickerAction>
    get() = EventFlow()

private suspend fun getActionPickerItems(
    actionRepository: ActionRepository,
    key: ActionPickerKey,
    resourceProvider: ResourceProvider
): List<ActionPickerItem> = buildList<ActionPickerItem> {
    val specialOptions = mutableListOf<ActionPickerItem.SpecialOption>()

    if (key.showDefaultOption) {
        specialOptions += ActionPickerItem.SpecialOption(
            title = resourceProvider.string(R.string.es_default),
            getResult = { ActionPickerKey.Result.Default }
        )
    }

    if (key.showNoneOption) {
        specialOptions += ActionPickerItem.SpecialOption(
            title = resourceProvider.string(R.string.es_none),
            getResult = { ActionPickerKey.Result.None }
        )
    }

    val actionsAndDelegates = (
            (actionRepository.getActionPickerDelegates()
                .map { ActionPickerItem.PickerDelegate(it) }) + (actionRepository.getAllActions()
                .map {
                    ActionPickerItem.ActionItem(
                        it,
                        actionRepository.getActionSettingsKey(it.id)
                    )
                })
            )
        .sortedBy { it.title }

    return specialOptions + actionsAndDelegates
}

sealed class ActionPickerItem {
    data class ActionItem(
        val action: Action,
        override val settingsKey: Key<Nothing>?,
    ) : ActionPickerItem() {
        override val title: String
            get() = action.title

        @Composable
        override fun icon(modifier: Modifier) {
            ActionIcon(action = action, modifier = modifier)
        }

        override suspend fun getResult() = ActionPickerKey.Result.Action(action.id)
    }

    @Immutable
    data class PickerDelegate(val delegate: ActionPickerDelegate) : ActionPickerItem() {
        override val title: String
            get() = delegate.title

        override val settingsKey: Key<Nothing>?
            get() = delegate.settingsKey

        @Composable
        override fun icon(modifier: Modifier) {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                delegate.icon()
            }
        }

        override suspend fun getResult() = delegate.getResult()
    }

    data class SpecialOption(
        override val title: String,
        val getResult: () -> ActionPickerKey.Result?
    ) : ActionPickerItem() {

        override val settingsKey: Key<Nothing>?
            get() = null

        @Composable
        override fun icon(modifier: Modifier) {
            Spacer(modifier)
        }

        override suspend fun getResult() = getResult.invoke()
    }

    abstract val title: String
    abstract val settingsKey: Key<Nothing>?

    @Composable
    abstract fun icon(modifier: Modifier = Modifier)

    abstract suspend fun getResult(): ActionPickerKey.Result?
}