/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.ui.picker

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*

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

@Provide val actionPickerUi = ModelKeyUi<ActionPickerKey, ActionPickerModel> {
  Scaffold(
    topBar = { TopAppBar(title = { Text(R.string.es_action_picker_title) }) }
  ) {
    ResourceVerticalListFor(model.items) { item ->
      ListItem(
        modifier = Modifier.clickable { model.pickAction(item) },
        leading = { item.Icon(Modifier.size(24.dp)) },
        trailing = if (item.settingsKey != null) ({
          IconButton(onClick = { model.openActionSettings(item) }) {
            Icon(R.drawable.es_ic_settings)
          }
        }) else null,
        title = { Text(item.title) }
      )
    }
  }
}

data class ActionPickerModel(
  val items: Resource<List<ActionPickerItem>>,
  val openActionSettings: (ActionPickerItem) -> Unit,
  val pickAction: (ActionPickerItem) -> Unit
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
        delegate.Icon()
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

  @Composable fun Icon() {
    Icon(Modifier)
  }

  @Composable abstract fun Icon(modifier: Modifier)

  abstract suspend fun getResult(): ActionPickerKey.Result?
}

@Provide fun actionPickerModel(
  filter: ActionFilter,
  permissionRequester: PermissionRequester,
  repository: ActionRepository,
  RP: ResourceProvider,
  ctx: KeyUiContext<ActionPickerKey>
) = ActionPickerModel(
  items = produceResource { getActionPickerItems() },
  openActionSettings = action { item -> ctx.navigator.push(item.settingsKey!!) },
  pickAction = action { item ->
    val result = item.getResult() ?: return@action
    if (result is ActionPickerKey.Result.Action) {
      val action = repository.getAction(result.actionId)
      if (!permissionRequester(action.permissions))
        return@action
    }
    ctx.navigator.pop(ctx.key, result)
  }
)

private suspend fun getActionPickerItems(
  @Inject filter: ActionFilter,
  key: ActionPickerKey,
  repository: ActionRepository,
  RP: ResourceProvider
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
      (repository.getActionPickerDelegates()
        .filter { filter(it.baseId) }
        .map { ActionPickerItem.PickerDelegate(it) }) + (repository.getAllActions()
        .filter { filter(it.id) }
        .map {
          ActionPickerItem.ActionItem(
            it,
            repository.getActionSettingsKey(it.id)
          )
        })
      )
    .sortedBy { it.title }

  return specialOptions + actionsAndDelegates
}
