/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide val unlockHomeItem = HomeItem("Unlock") { UnlockScreen() }

class UnlockScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      deviceScreenManager: DeviceScreenManager,
      scope: ScopedCoroutineScope<ScreenScope>,
      toaster: Toaster
    ) = Ui<UnlockScreen> {
      ScreenScaffold(topBar = { AppBar { Text("Unlock") } }) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Button(
            onClick = {
              scope.launch {
                toaster.toast("Turn the screen off")
                deviceScreenManager.screenState.first { !it.isOn }
                delay(1500)
                val unlocked = deviceScreenManager.unlockScreen()
                toaster.toast("Screen unlocked $unlocked")
              }
            }
          ) { Text("Unlock") }

          Spacer(Modifier.height(8.dp))

          Button(
            onClick = {
              scope.launch {
                toaster.toast("Turn the screen off")
                deviceScreenManager.screenState.first { !it.isOn }
                delay(1500)
                val screenOn = deviceScreenManager.turnScreenOn()
                toaster.toast("Screen activated $screenOn")
              }
            }
          ) { Text("Activate") }
        }
      }
    }
  }
}
