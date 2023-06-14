/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.bind
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide

@Provide val torchHomeItem = HomeItem("Torch") { TorchScreen() }

class TorchScreen : Screen<Unit>

@Provide fun torchUi(torchManager: TorchManager) = Ui<TorchScreen, Unit> {
  Scaffold(topBar = { TopAppBar(title = { Text("Torch") }) }) {
    Column(
      modifier = Modifier.center(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        "Torch is ${if (torchManager.torchEnabled.bind()) "enabled" else "disabled"}",
        style = MaterialTheme.typography.headlineMedium
      )

      Spacer(Modifier.height(8.dp))

      Button(onClick = action { torchManager.setTorchState(!torchManager.torchEnabled.value) }) {
        Text("Toggle torch")
      }
    }
  }
}
