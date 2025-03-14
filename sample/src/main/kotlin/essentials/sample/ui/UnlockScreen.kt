/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import essentials.*
import essentials.compose.*
import essentials.coroutines.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.util.*
import essentials.util.ScreenState
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide val unlockHomeItem = HomeItem("Unlock") { UnlockScreen() }

class UnlockScreen : Screen<Unit>

@Provide @Composable fun UnlockUi(
  screenStateProducer: @Composable () -> ScreenState,
  scope: Scope<*> = inject
): Ui<UnlockScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Unlock") } }) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Button(
        onClick = {
          coroutineScope().launch {
            showToast("Turn the screen off")
            moleculeFlow { screenStateProducer() }.first { !it.isOn }
            delay(1500)
            val unlocked = unlockScreen()
            showToast("Screen unlocked $unlocked")
          }
        }
      ) { Text("Unlock") }

      Spacer(Modifier.height(8.dp))

      Button(
        onClick = {
          coroutineScope().launch {
            showToast("Turn the screen off")
            moleculeFlow { screenStateProducer() }.first { !it.isOn }
            delay(1500)
            val screenOn = turnScreenOn()
            showToast("Screen activated $screenOn")
          }
        }
      ) { Text("Activate") }
    }
  }
}
