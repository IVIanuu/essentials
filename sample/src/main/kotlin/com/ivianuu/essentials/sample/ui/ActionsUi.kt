/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.gestures.action.ActionRepository
import com.ivianuu.essentials.gestures.action.ExecuteActionUseCase
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.ComponentScope

@Provide val actionsHomeItem = HomeItem("Actions") { ActionsKey }

object ActionsKey : Key<Unit>

@Provide fun actionsUi(
  actionRepository: ActionRepository,
  executeAction: ExecuteActionUseCase,
  navigator: Navigator,
  S: ComponentScope<UiComponent>,
  T: Toaster
): KeyUi<ActionsKey> = {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Actions") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        launch {
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
