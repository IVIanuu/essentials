/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.compose.LocalScope
import com.ivianuu.essentials.coroutines.coroutineScope
import com.ivianuu.essentials.gestures.action.ActionRepository
import com.ivianuu.essentials.gestures.action.ExecuteActionUseCase
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerScreen
import com.ivianuu.essentials.safeAs
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

@Provide val actionsHomeItem = HomeItem("Actions") { ActionsScreen() }

class ActionsScreen : Screen<Unit>

@Provide fun actionsUi(
  navigator: Navigator,
  repository: ActionRepository,
  executeAction: ExecuteActionUseCase,
  toaster: Toaster
) = Ui<ActionsScreen, Unit> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Actions") }) }
  ) {
    val scope = LocalScope.current.coroutineScope
    Button(
      modifier = Modifier.center(),
      onClick = {
        scope.launch {
          val actionId = navigator.push(ActionPickerScreen())
            .safeAs<ActionPickerScreen.Result.Action>()
            ?.actionId ?: return@launch

          val action = repository.getAction(actionId)

          toaster("Execute action ${action.title}")

          executeAction(actionId)
        }
      }
    ) { Text("Pick action") }
  }
}
