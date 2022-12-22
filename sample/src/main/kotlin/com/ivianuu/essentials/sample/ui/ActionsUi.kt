/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.gestures.action.ActionRepository
import com.ivianuu.essentials.gestures.action.ExecuteActionUseCase
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.safeAs
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.launch

@Provide val actionsHomeItem = HomeItem("Actions") { ActionsKey }

object ActionsKey : Key<Unit>

context(NamedCoroutineScope<UiScope>, Toaster) @Provide fun actionsUi(
  actionRepository: ActionRepository,
  executeAction: ExecuteActionUseCase,
  navigator: Navigator
) = SimpleKeyUi<ActionsKey> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Actions") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        launch {
          val actionId = navigator.push(ActionPickerKey())
            .safeAs<ActionPickerKey.Result.Action>()
            ?.actionId ?: return@launch

          val action = actionRepository.getAction(actionId)

          showToast("Execute action ${action.title}")

          executeAction(actionId)
        }
      }
    ) { Text("Pick action") }
  }
}
