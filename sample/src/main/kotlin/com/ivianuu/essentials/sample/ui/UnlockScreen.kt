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
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.ScreenScope
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.util.DeviceScreenManager
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Provide val unlockHomeItem = HomeItem("Unlock") { UnlockScreen() }

class UnlockScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      deviceScreenManager: DeviceScreenManager,
      scope: ScopedCoroutineScope<ScreenScope>,
      toaster: Toaster
    ) = Ui<UnlockScreen, Unit> {
      ScreenScaffold(topBar = { AppBar { Text("Unlock") } }) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Button(
            onClick = {
              scope.launch {
                toaster("Turn the screen off")
                deviceScreenManager.screenState.first { !it.isOn }
                delay(1500)
                val unlocked = deviceScreenManager.unlockScreen()
                toaster("Screen unlocked $unlocked")
              }
            }
          ) { Text("Unlock") }

          Spacer(Modifier.height(8.dp))

          Button(
            onClick = {
              scope.launch {
                toaster("Turn the screen off")
                deviceScreenManager.screenState.first { !it.isOn }
                delay(1500)
                val screenOn = deviceScreenManager.turnScreenOn()
                toaster("Screen activated $screenOn")
              }
            }
          ) { Text("Activate") }
        }
      }
    }
  }
}
