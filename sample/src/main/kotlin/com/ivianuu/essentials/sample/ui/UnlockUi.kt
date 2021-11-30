/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.screenstate.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.unlock.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide val unlockHomeItem = HomeItem("Unlock") { UnlockKey }

object UnlockKey : Key<Unit>

@Provide fun unlockUi(
  screenState: Flow<ScreenState>,
  screenActivator: ScreenActivator,
  screenUnlocker: ScreenUnlocker,
  S: NamedCoroutineScope<KeyUiScope>,
  T: ToastContext
) = KeyUi<UnlockKey> {
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
