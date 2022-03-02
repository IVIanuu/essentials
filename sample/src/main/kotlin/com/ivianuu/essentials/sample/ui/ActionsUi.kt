/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import androidx.compose.ui.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.picker.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*

@Provide val actionsHomeItem = HomeItem("Actions") { ActionsKey }

object ActionsKey : Key<Unit>

@Provide fun actionsUi(
  actionRepository: ActionRepository,
  executeAction: ExecuteActionUseCase,
  navigator: Navigator,
  scope: NamedCoroutineScope<UiScope>,
  T: Toaster
) = KeyUi<ActionsKey> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Actions") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        scope.launch {
          val actionId = navigator.push(ActionPickerKey())
            ?.let { it as? ActionPickerKey.Result.Action }
            ?.actionId ?: return@launch

          val action = actionRepository.getAction(actionId)

          showToast("Execute action ${action.title}")

          executeAction(actionId)
        }
      }
    ) { Text("Pick action") }
  }
}
