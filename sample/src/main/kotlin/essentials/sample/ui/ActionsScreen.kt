/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import essentials.*
import essentials.compose.*
import essentials.gestures.action.*
import essentials.gestures.action.ui.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

@Provide val actionsHomeItem = HomeItem("Actions") { ActionsScreen() }

class ActionsScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      repository: ActionRepository,
      toaster: Toaster
    ) = Ui<ActionsScreen> {
      EsScaffold(topBar = { EsAppBar { Text("Actions") } }) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Button(
            onClick = scopedAction {
              val actionId = navigator.push(ActionPickerScreen())
                .safeAs<ActionPickerScreen.Result.Action>()
                ?.actionId ?: return@scopedAction

              val action = repository.getAction(actionId)

              delay(1.seconds)

              toaster.toast("Execute action ${action.title}")

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
                toaster.toast("Execute action ${action.title}")

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
