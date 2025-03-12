/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import essentials.compose.moleculeFlow
import essentials.coroutines.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide val unlockHomeItem = HomeItem("Unlock") { UnlockScreen() }

class UnlockScreen : Screen<Unit>

@Provide @Composable fun UnlockUi(
  deviceScreenManager: DeviceScreenManager,
  scope: ScopedCoroutineScope<ScreenScope>,
  showToast: showToast
): Ui<UnlockScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Unlock") } }) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Button(
        onClick = {
          scope.launch {
            showToast("Turn the screen off")
            moleculeFlow { deviceScreenManager.screenState() }.first { !it.isOn }
            delay(1500)
            val unlocked = deviceScreenManager.unlockScreen()
            showToast("Screen unlocked $unlocked")
          }
        }
      ) { Text("Unlock") }

      Spacer(Modifier.height(8.dp))

      Button(
        onClick = {
          scope.launch {
            showToast("Turn the screen off")
            moleculeFlow { deviceScreenManager.screenState() }.first { !it.isOn }
            delay(1500)
            val screenOn = deviceScreenManager.turnScreenOn()
            showToast("Screen activated $screenOn")
          }
        }
      ) { Text("Activate") }
    }
  }
}

