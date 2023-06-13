/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.unlock.ScreenActivator
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Provide val unlockHomeItem = HomeItem("Unlock") { UnlockKey() }

class UnlockKey : Key<Unit>

@Provide fun unlockUi(
  resources: Resources,
  screenState: Flow<ScreenState>,
  screenActivator: ScreenActivator,
  screenUnlocker: ScreenUnlocker,
  scope: ScopedCoroutineScope<KeyUiScope>,
  toaster: Toaster
) = Ui<UnlockKey, Unit> { model ->
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
          scope.launch {
            toaster("Turn the screen off")
            screenState.first { !it.isOn }
            delay(1500)
            val unlocked = screenUnlocker()
            toaster("Screen unlocked $unlocked")
          }
        }
      ) { Text("Unlock") }

      Spacer(Modifier.height(8.dp))

      Button(
        onClick = {
          scope.launch {
            toaster("Turn the screen off")
            screenState.first { !it.isOn }
            delay(1500)
            val activated = screenActivator()
            toaster("Screen activated $activated")
          }
        }
      ) { Text("Activate") }
    }
  }
}
