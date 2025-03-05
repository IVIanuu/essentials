/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastMap
import essentials.compose.*
import essentials.gestures.action.*
import essentials.permission.*
import essentials.resource.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

class ActionPickerScreen(
  val showDefaultOption: Boolean = false,
  val showNoneOption: Boolean = false,
) : Screen<ActionPickerScreen.Result> {
  sealed interface Result {
    data class Action(val actionId: String) : Result
    data object Default : Result
    data object None : Result
  }

  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      permissionManager: PermissionManager,
      repository: ActionRepository,
      screen: ActionPickerScreen
    ) = Ui<ActionPickerScreen> {
      val items by produceScopedState(Resource.Idle()) {
        value = catchResource {
          buildList<ActionPickerItem> {
            if (screen.showDefaultOption)
              this += ActionPickerItem.SpecialOption(title = "Default", getResult = { Result.Default })

            if (screen.showNoneOption)
              this += ActionPickerItem.SpecialOption(title = "None", getResult = { Result.None })

            this += (
                (repository.getActionPickerDelegates()
                  .fastMap { ActionPickerItem.PickerDelegate(it) }) + (repository.getAllActions()
                  .fastMap {
                    ActionPickerItem.ActionItem(
                      it,
                      repository.getActionSettingsKey(it.id)
                    )
                  }))
              .sortedBy { it.title }
          }
        }
      }

      EsScaffold(topBar = { EsAppBar { Text("Pick an action") } }) {
        ResourceBox(items) { items ->
          EsLazyColumn {
            items(items) { item ->
              EsListItem(
                onClick = scopedAction {
                  val result = item.getResult(navigator)
                    ?: return@scopedAction
                  if (result is Result.Action) {
                    val action = repository.getAction(result.actionId)
                    if (!permissionManager.ensurePermissions(action.permissions))
                      return@scopedAction
                  }
                  navigator.pop(screen, result)
                },
                leadingContent = { item.Icon(Modifier.size(24.dp)) },
                trailingContent = if (item.settingsScreen == null) null
                else ({
                  IconButton(onClick = scopedAction { navigator.push(item.settingsScreen!!) }) {
                    Icon(Icons.Default.Settings, null)
                  }
                }),
                headlineContent = { Text(item.title) }
              )
            }
          }
        }
      }
    }
  }
}

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

    override suspend fun getResult(navigator: Navigator) =
      ActionPickerScreen.Result.Action(action.id)
  }

  class PickerDelegate(private val delegate: ActionPickerDelegate) : ActionPickerItem {
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
