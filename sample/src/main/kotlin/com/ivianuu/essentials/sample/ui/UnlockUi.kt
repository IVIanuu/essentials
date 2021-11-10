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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.unlock.ScreenActivator
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Provide val unlockHomeItem = HomeItem("Unlock") { UnlockKey }

object UnlockKey : Key<Unit>

@Provide fun unlockUi(
  screenState: Flow<ScreenState>,
  screenActivator: ScreenActivator,
  screenUnlocker: ScreenUnlocker,
  S: ComponentScope<KeyUiComponent>,
  T: ToastContext
): KeyUi<UnlockKey> = {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Unlock") }) }
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Button(
        onClick = {
          launch {
            showToast("Turn the screen off")
            screenState.first { !it.isOn }
            delay(3000)
            val unlocked = screenUnlocker()
            showToast("Screen unlocked $unlocked")
          }
        }
      ) { Text("Unlock") }

      Spacer(Modifier.height(8.dp))

      Button(
        onClick = {
          launch {
            showToast("Turn the screen off")
            screenState.first { !it.isOn }
            delay(3000)
            val activated = screenActivator()
            showToast("Screen activated $activated")
          }
        }
      ) { Text("Activate") }
    }
  }
}
