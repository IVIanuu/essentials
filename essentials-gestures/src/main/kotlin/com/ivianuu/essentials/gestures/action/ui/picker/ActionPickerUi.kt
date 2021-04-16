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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.GetActionPickerDelegatesUseCase
import com.ivianuu.essentials.gestures.action.GetActionSettingsKeyUseCase
import com.ivianuu.essentials.gestures.action.GetActionUseCase
import com.ivianuu.essentials.gestures.action.GetAllActionsUseCase
import com.ivianuu.essentials.gestures.action.ui.ActionIcon
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.resourceFlow
import com.ivianuu.essentials.store.StateBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.util.StringResourceProvider
import com.ivianuu.injekt.Given

class ActionPickerKey(
    val showDefaultOption: Boolean = false,
    val showNoneOption: Boolean = false,
) : Key<ActionPickerKey.Result> {
    sealed class Result {
        data class Action(val actionId: String) : Result()
        object Default : Result()
        object None : Result()
    }
}

@Given
val actionPickerUi: ModelKeyUi<ActionPickerKey, ActionPickerModel> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_action_picker_title)) }) }
    ) {
        ResourceLazyColumnFor(model.items) { item ->
            ListItem(
                leading = { item.Icon(Modifier.size(24.dp)) },
                trailing = if (item.settingsKey != null) ({
                    IconButton(onClick = { model.openActionSettings(item) }) {
                        Icon(painterResource(R.drawable.es_ic_settings), null)
                    }
                }) else null,
                title = { Text(item.title) },
                onClick = { model.pickAction(item) }
            )
        }
    }
}

@Optics
data class ActionPickerModel(
    val items: Resource<List<ActionPickerItem>> = Idle,
    val openActionSettings: (ActionPickerItem) -> Unit = {},
    val pickAction: (ActionPickerItem) -> Unit = {}
)

@Given
fun actionPickerModel(
    @Given getAction: GetActionUseCase,
    @Given getActionPickerItems: GetActionPickerItemsUseCase,
    @Given key: ActionPickerKey,
    @Given navigator: Navigator,
    @Given permissionRequester: PermissionRequester
): StateBuilder<KeyUiGivenScope, ActionPickerModel> = {
    resourceFlow { emit(getActionPickerItems()) }
        .updateIn(this) { copy(items = it) }

    action(ActionPickerModel.openActionSettings()) { item -> navigator.push(item.settingsKey!!) }

    action(ActionPickerModel.pickAction()) { item ->
        val result = item.getResult() ?: return@action
        if (result is ActionPickerKey.Result.Action) {
            val action = getAction(result.actionId)!!
            if (!permissionRequester(action.permissions))
                return@action
        }
        navigator.pop(key, result)
    }
}

sealed class ActionPickerItem {
    data class ActionItem(
        val action: Action<*>,
        override val settingsKey: Key<Nothing>?,
    ) : ActionPickerItem() {
        override val title: String
            get() = action.title

        @Composable
        override fun Icon(modifier: Modifier) {
            ActionIcon(action = action, modifier = modifier)
        }

        override suspend fun getResult() = ActionPickerKey.Result.Action(action.id)
    }

    data class PickerDelegate(val delegate: ActionPickerDelegate) : ActionPickerItem() {
        override val title: String
            get() = delegate.title

        override val settingsKey: Key<Nothing>?
            get() = delegate.settingsKey

        @Composable
        override fun Icon(modifier: Modifier) {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                delegate.icon()
            }
        }

        override suspend fun getResult() = delegate.pickAction()
    }

    data class SpecialOption(
        override val title: String,
        val getResult: () -> ActionPickerKey.Result?
    ) : ActionPickerItem() {
        override val settingsKey: Key<Nothing>?
            get() = null

        @Composable
        override fun Icon(modifier: Modifier) {
            Spacer(modifier)
        }

        override suspend fun getResult() = getResult.invoke()
    }

    abstract val title: String
    abstract val settingsKey: Key<Nothing>?

    @Composable
    abstract fun Icon(modifier: Modifier = Modifier)

    abstract suspend fun getResult(): ActionPickerKey.Result?
}

private typealias GetActionPickerItemsUseCase = suspend () -> List<ActionPickerItem>

@Given
fun getActionPickerItemsUseCase(
    @Given getActionPickerDelegates: GetActionPickerDelegatesUseCase,
    @Given getAllActions: GetAllActionsUseCase,
    @Given getActionSettingsKey: GetActionSettingsKeyUseCase,
    @Given key: ActionPickerKey,
    @Given stringResource: StringResourceProvider
): GetActionPickerItemsUseCase = {
    val specialOptions = mutableListOf<ActionPickerItem.SpecialOption>()

    if (key.showDefaultOption) {
        specialOptions += ActionPickerItem.SpecialOption(
            title = stringResource(R.string.es_default, emptyList()),
            getResult = { ActionPickerKey.Result.Default }
        )
    }

    if (key.showNoneOption) {
        specialOptions += ActionPickerItem.SpecialOption(
            title = stringResource(R.string.es_none, emptyList()),
            getResult = { ActionPickerKey.Result.None }
        )
    }

    val actionsAndDelegates = (
            (getActionPickerDelegates()
                .map { ActionPickerItem.PickerDelegate(it) }) + (getAllActions()
                .map {
                    ActionPickerItem.ActionItem(
                        it,
                        getActionSettingsKey(it.id)
                    )
                })
            )
        .sortedBy { it.title }

    specialOptions + actionsAndDelegates
}
