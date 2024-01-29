/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import com.ivianuu.essentials.compose.LocalScope
import com.ivianuu.essentials.coroutineScope
import com.ivianuu.essentials.gestures.action.ActionRepository
import com.ivianuu.essentials.gestures.action.ui.ActionPickerScreen
import com.ivianuu.essentials.safeAs
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Provide val actionsHomeItem = HomeItem("Actions") { ActionsScreen() }

class ActionsScreen : Screen<Unit>

@Provide fun actionsUi(
  navigator: Navigator,
  repository: ActionRepository,
  toaster: Toaster
) = Ui<ActionsScreen, Unit> {
  Scaffold(topBar = { AppBar { Text("Actions") } }) {
    val scope = LocalScope.current.coroutineScope
    Column {
      Button(
        onClick = {
          scope.launch {
            val actionId = navigator.push(ActionPickerScreen())
              .safeAs<ActionPickerScreen.Result.Action>()
              ?.actionId ?: return@launch

            val action = repository.getAction(actionId)

            delay(1.seconds)

            toaster("Execute action ${action.title}")

            repository.executeAction(actionId)
          }
        }
      ) { Text("Pick action") }

      Button(
        onClick = {
          scope.launch {
            val actionId = navigator.push(ActionPickerScreen())
              .safeAs<ActionPickerScreen.Result.Action>()
              ?.actionId ?: return@launch

            val action = repository.getAction(actionId)

            delay(1.seconds)

            while (true) {
              toaster("Execute action ${action.title}")

              repository.executeAction(actionId)

              delay(3.seconds)
            }
          }
        }
      ) { Text("Loop action") }
    }
  }
}
