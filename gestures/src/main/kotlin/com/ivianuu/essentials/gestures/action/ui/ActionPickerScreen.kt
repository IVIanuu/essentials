/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*

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
      @Inject repository: ActionRepository,
      screen: ActionPickerScreen
    ) = Ui<ActionPickerScreen, Unit> {
      val items = collectResource {
        buildList<ActionPickerItem> {
          if (screen.showDefaultOption)
            this += ActionPickerItem.SpecialOption(title = "Default", getResult = { Result.Default })

          if (screen.showNoneOption)
            this += ActionPickerItem.SpecialOption(title = "None", getResult = { Result.None })

          this += (
              (repository.getActionPickerDelegates()
                .map { ActionPickerItem.PickerDelegate(it) }) + (repository.getAllActions()
                .map {
                  ActionPickerItem.ActionItem(
                    it,
                    repository.getActionSettingsKey(it.id)
                  )
                }))
            .sortedBy { it.title }
        }
          .let { emit(it) }
      }

      ScreenScaffold(topBar = { AppBar { Text("Pick an action") } }) {
        ResourceVerticalListFor(items) { item ->
          ListItem(
            onClick = scopedAction {
              val result = item.getResult(navigator) ?: return@scopedAction
              if (result is Result.Action) {
                val action = repository.getAction(result.actionId)
                if (!permissionManager.requestPermissions(action.permissions))
                  return@scopedAction
              }
              navigator.pop(screen, result)
            },
            leading = { item.Icon(Modifier.size(24.dp)) },
            trailing = if (item.settingsScreen == null) null
            else ({
              IconButton(onClick = action { navigator.push(item.settingsScreen!!) }) {
                Icon(painterResource(com.ivianuu.essentials.android.R.drawable.ic_settings), null)
              }
            }),
            title = { Text(item.title) }
          )
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
