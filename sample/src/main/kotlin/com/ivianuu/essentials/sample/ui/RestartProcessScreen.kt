/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.processrestart.ProcessRestarter
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.ScreenScope
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

@Provide val restartProcessHomeItem = HomeItem("Restart process") { RestartProcessScreen() }

class RestartProcessScreen : Screen<Unit>

@Provide fun restartProcessUi(
  processRestarter: ProcessRestarter,
  scope: ScopedCoroutineScope<ScreenScope<*>>
) = Ui<RestartProcessScreen, Unit> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Restart process") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        scope.launch {
          processRestarter()
        }
      }
    ) {
      Text("Restart process")
    }
  }
}
