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

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.picker.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*

@Provide val actionsHomeItem = HomeItem("Actions") { ActionsKey }

object ActionsKey : Key<Nothing>

@Provide fun actionsUi(
  executeAction: ExecuteActionUseCase,
  getAction: GetActionUseCase,
  navigator: Navigator,
  scope: InjektCoroutineScope<UiScope>,
  toaster: Toaster
): KeyUi<ActionsKey> = {
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

          val action = getAction(actionId)!!

          showToast("Execute action \$${action.title}\$")

          executeAction(actionId)
        }
      }
    ) { Text("Pick action") }
  }
}
