/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.ui.picker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionRepository
import com.ivianuu.essentials.gestures.action.ui.ActionIcon
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.produceResource
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.injekt.Provide

data class ActionPickerKey(
  val showDefaultOption: Boolean = false,
  val showNoneOption: Boolean = false,
) : Key<ActionPickerKey.Result> {
  sealed interface Result {
    data class Action(val actionId: String) : Result
    object Default : Result
    object None : Result
  }
}

@Provide val actionPickerUi = ModelKeyUi<ActionPickerKey, ActionPickerModel> {
  Scaffold(
    topBar = { TopAppBar(title = { Text(R.string.es_action_picker_title) }) }
  ) {
    ResourceVerticalListFor(items) { item ->
      ListItem(
        modifier = Modifier.clickable { pickAction(item) },
        leading = { item.Icon(Modifier.size(24.dp)) },
        trailing = if (item.settingsKey != null) ({
          IconButton(onClick = { openActionSettings(item) }) {
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

sealed interface ActionPickerItem {
  class ActionItem(
    val action: Action<*>,
    override val settingsKey: Key<Unit>?,
  ) : ActionPickerItem {
    override val title: String
      get() = action.title

    @Composable override fun Icon(modifier: Modifier) {
      ActionIcon(action = action, modifier = modifier)
    }

    override suspend fun getResult() = ActionPickerKey.Result.Action(action.id)
  }

  class PickerDelegate(val delegate: ActionPickerDelegate) : ActionPickerItem {
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

  class SpecialOption(
    override val title: String,
    val getResult: () -> ActionPickerKey.Result?
  ) : ActionPickerItem {
    override val settingsKey: Key<Unit>?
      get() = null

    @Composable override fun Icon(modifier: Modifier) {
      Spacer(modifier)
    }

    override suspend fun getResult() = getResult.invoke()
  }

  val title: String
  val settingsKey: Key<Unit>?

  @Composable fun Icon() {
    Icon(Modifier)
  }

  @Composable fun Icon(modifier: Modifier)

  suspend fun getResult(): ActionPickerKey.Result?
}

context(ActionRepository, ResourceProvider) @Provide fun actionPickerModel(
  filter: ActionFilter,
  permissionRequester: PermissionRequester,
  ctx: KeyUiContext<ActionPickerKey>
) = Model {
  ActionPickerModel(
    items = produceResource { getActionPickerItems(ctx.key, filter) },
    openActionSettings = action { item -> ctx.navigator.push(item.settingsKey!!) },
    pickAction = action { item ->
      val result = item.getResult() ?: return@action
      if (result is ActionPickerKey.Result.Action) {
        val action = getAction(result.actionId)
        if (!permissionRequester(action.permissions))
          return@action
      }
      ctx.navigator.pop(ctx.key, result)
    }
  )
}

context(ActionRepository, ResourceProvider) private suspend fun getActionPickerItems(
  key: ActionPickerKey,
  filter: ActionFilter
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
