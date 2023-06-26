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
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionRepository
import com.ivianuu.essentials.gestures.action.ui.ActionIcon
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.produceResourceState
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

class ActionPickerScreen(
  val showDefaultOption: Boolean = false,
  val showNoneOption: Boolean = false,
) : Screen<ActionPickerScreen.Result> {
  sealed interface Result {
    data class Action(val actionId: String) : Result
    object Default : Result
    object None : Result
  }
}

@Provide val actionPickerUi = Ui<ActionPickerScreen, ActionPickerModel> { model ->
  Scaffold(
    topBar = { TopAppBar(title = { Text(R.string.es_action_picker_title) }) }
  ) {
    ResourceVerticalListFor(model.items) { item ->
      ListItem(
        modifier = Modifier.clickable { model.pickAction(item) },
        leading = { item.Icon(Modifier.size(24.dp)) },
        trailing = if (item.settingsScreen != null) ({
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

sealed interface ActionPickerItem {
  class ActionItem(
    val action: Action<*>,
    override val settingsScreen: Screen<Unit>?,
  ) : ActionPickerItem {
    override val title: String
      get() = action.title

    @Composable override fun Icon(modifier: Modifier) {
      ActionIcon(action = action, modifier = modifier)
    }

    override suspend fun getResult(navigator: Navigator) = ActionPickerScreen.Result.Action(action.id)
  }

  class PickerDelegate(val delegate: ActionPickerDelegate) : ActionPickerItem {
    override val title: String
      get() = delegate.title

    override val settingsScreen: Screen<Unit>?
      get() = delegate.settingsScreen

    @Composable override fun Icon(modifier: Modifier) {
      Box(modifier = modifier, contentAlignment = Alignment.Center) {
        delegate.icon()
      }
    }

    override suspend fun getResult(navigator: Navigator) = delegate.pickAction(navigator)
  }

  class SpecialOption(
    override val title: String,
    val getResult: () -> ActionPickerScreen.Result?
  ) : ActionPickerItem {
    override val settingsScreen: Screen<Unit>?
      get() = null

    @Composable override fun Icon(modifier: Modifier) {
      Spacer(modifier)
    }

    override suspend fun getResult(navigator: Navigator) = getResult.invoke()
  }

  val title: String
  val settingsScreen: Screen<Unit>?

  @Composable fun Icon() {
    Icon(Modifier)
  }

  @Composable fun Icon(modifier: Modifier)

  suspend fun getResult(navigator: Navigator): ActionPickerScreen.Result?
}

@Provide fun actionPickerModel(
  filter: ActionFilter,
  navigator: Navigator,
  permissionManager: PermissionManager,
  repository: ActionRepository,
  resources: Resources,
  screen: ActionPickerScreen
) = Model {
  ActionPickerModel(
    items = produceResourceState {
      emit(getActionPickerItems(filter, screen))
    }.value,
    openActionSettings = action { item -> navigator.push(item.settingsScreen!!) },
    pickAction = action { item ->
      val result = item.getResult(navigator) ?: return@action
      if (result is ActionPickerScreen.Result.Action) {
        val action = repository.getAction(result.actionId)
        if (!permissionManager.requestPermissions(action.permissions))
          return@action
      }
      navigator.pop(screen, result)
    }
  )
}

private suspend fun getActionPickerItems(
  filter: ActionFilter,
  screen: ActionPickerScreen,
  @Inject repository: ActionRepository,
  @Inject resources: Resources
): List<ActionPickerItem> {
  val specialOptions = mutableListOf<ActionPickerItem.SpecialOption>()

  if (screen.showDefaultOption) {
    specialOptions += ActionPickerItem.SpecialOption(
      title = resources(R.string.es_default),
      getResult = { ActionPickerScreen.Result.Default }
    )
  }

  if (screen.showNoneOption) {
    specialOptions += ActionPickerItem.SpecialOption(
      title = resources(R.string.es_none),
      getResult = { ActionPickerScreen.Result.None }
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
