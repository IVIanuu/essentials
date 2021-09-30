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
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Provide val unlockHomeItem = HomeItem("Unlock") { UnlockKey }

object UnlockKey : Key<Unit>

@Provide fun unlockUi(
  screenState: Flow<ScreenState>,
  screenUnlocker: ScreenUnlocker,
  scope: NamedCoroutineScope<KeyUiScope>,
  toaster: Toaster,
): KeyUi<UnlockKey> = {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Unlock") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        scope.launch {
          showToast("Turn the screen off and on")
          screenState.first { it == ScreenState.LOCKED }
          val unlocked = screenUnlocker()
          showToast("Screen unlocked $unlocked")
        }
      }
    ) { Text("Unlock") }
  }
}
