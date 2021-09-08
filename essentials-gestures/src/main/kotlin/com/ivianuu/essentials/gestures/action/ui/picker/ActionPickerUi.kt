/*
 * Copyright 2021 Manuel Wrage
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

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

data class ActionPickerKey(
  val showDefaultOption: Boolean = false,
  val showNoneOption: Boolean = false,
) : Key<ActionPickerKey.Result> {
  sealed class Result {
    data class Action(val actionId: String) : Result()
    object Default : Result()
    object None : Result()
  }
}

@Provide val actionPickerUi: ModelKeyUi<ActionPickerKey, ActionPickerModel> = {
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

@Optics data class ActionPickerModel(
  val items: Resource<List<ActionPickerItem>> = Idle,
  val openActionSettings: (ActionPickerItem) -> Unit = {},
  val pickAction: (ActionPickerItem) -> Unit = {}
)

sealed class ActionPickerItem {
  data class ActionItem(
    val action: Action<*>,
    override val settingsKey: Key<Unit>?,
  ) : ActionPickerItem() {
    override val title: String
      get() = action.title

    @Composable override fun Icon(modifier: Modifier) {
      ActionIcon(action = action, modifier = modifier)
    }

    override suspend fun getResult() = ActionPickerKey.Result.Action(action.id)
  }

  data class PickerDelegate(val delegate: ActionPickerDelegate) : ActionPickerItem() {
    override val title: String
      get() = delegate.title

    override val settingsKey: Key<Unit>?
      get() = delegate.settingsKey

    @Composable override fun Icon(modifier: Modifier) {
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
    override val settingsKey: Key<Unit>?
      get() = null

    @Composable override fun Icon(modifier: Modifier) {
      Spacer(modifier)
    }

    override suspend fun getResult() = getResult.invoke()
  }

  abstract val title: String
  abstract val settingsKey: Key<Unit>?

  // todo remove overload once compose supports abstract params with default value
  @Composable fun Icon() {
    Icon(Modifier)
  }

  @Composable abstract fun Icon(modifier: Modifier)

  abstract suspend fun getResult(): ActionPickerKey.Result?
}

@Provide fun actionPickerModel(
  filter: ActionFilter,
  getAction: GetActionUseCase,
  getActionPickerDelegates: GetActionPickerDelegatesUseCase,
  getAllActions: GetAllActionsUseCase,
  getActionSettings: GetActionSettingsKeyUseCase,
  key: ActionPickerKey,
  navigator: Navigator,
  permissionRequester: PermissionRequester,
  rp: ResourceProvider,
  scope: InjektCoroutineScope<KeyUiScope>,
): @Scoped<KeyUiScope> StateFlow<ActionPickerModel> = scope.state(ActionPickerModel()) {
  resourceFlow { emit(getActionPickerItems()) }.update { copy(items = it) }
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

private suspend fun getActionPickerItems(
  @Inject filter: ActionFilter,
  @Inject getActionPickerDelegates: GetActionPickerDelegatesUseCase,
  @Inject getAllActions: GetAllActionsUseCase,
  @Inject getActionSettingsKey: GetActionSettingsKeyUseCase,
  @Inject key: ActionPickerKey,
  @Inject rp: ResourceProvider
): List<ActionPickerItem> {
  val specialOptions = mutableListOf<ActionPickerItem.SpecialOption>()

  if (key.showDefaultOption) {
    specialOptions += ActionPickerItem.SpecialOption(
      title = loadResource(R.string.es_default),
      getResult = { ActionPickerKey.Result.Default }
    )
  }

  if (key.showNoneOption) {
    specialOptions += ActionPickerItem.SpecialOption(
      title = loadResource(R.string.es_none),
      getResult = { ActionPickerKey.Result.None }
    )
  }

  val actionsAndDelegates = (
      (getActionPickerDelegates()
        .filter { filter(it.baseId) }
        .map { ActionPickerItem.PickerDelegate(it) }) + (getAllActions()
        .filter { filter(it.id) }
        .map {
          ActionPickerItem.ActionItem(
            it,
            getActionSettingsKey(it.id)
          )
        })
      )
    .sortedBy { it.title }

  return specialOptions + actionsAndDelegates
}
