/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

@Provide val actionsHomeItem = HomeItem("Actions") { ActionsScreen() }

class ActionsScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      repository: ActionRepository,
      toaster: Toaster
    ) = Ui<ActionsScreen, Unit> {
      ScreenScaffold(topBar = { AppBar { Text("Actions") } }) {
        Column {
          Button(
            onClick = scopedAction {
              val actionId = navigator.push(ActionPickerScreen())
                .safeAs<ActionPickerScreen.Result.Action>()
                ?.actionId ?: return@scopedAction

              val action = repository.getAction(actionId)

              delay(1.seconds)

              toaster("Execute action ${action.title}")

              repository.executeAction(actionId)
            }
          ) { Text("Pick action") }

          Button(
            onClick = scopedAction {
              val actionId = navigator.push(ActionPickerScreen())
                .safeAs<ActionPickerScreen.Result.Action>()
                ?.actionId ?: return@scopedAction

              val action = repository.getAction(actionId)

              delay(1.seconds)

              while (true) {
                toaster("Execute action ${action.title}")

                repository.executeAction(actionId)

                delay(3.seconds)
              }
            }
          ) { Text("Loop action") }
        }
      }
    }
  }
}
