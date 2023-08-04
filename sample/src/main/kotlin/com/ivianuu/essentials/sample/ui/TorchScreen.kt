/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide

@Provide val torchHomeItem = HomeItem("Torch") { TorchScreen() }

class TorchScreen : Screen<Unit>

@Provide fun torchUi(torchManager: TorchManager) = Ui<TorchScreen, Unit> {
  Scaffold(topBar = { AppBar { Text("Torch") } }) {
    Column(
      modifier = Modifier.center(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        "Torch is ${if (torchManager.torchEnabled.collectAsState().value) "enabled" else "disabled"}",
        style = MaterialTheme.typography.h4
      )

      Spacer(Modifier.height(8.dp))

      Button(onClick = action { torchManager.updateTorchState(!torchManager.torchEnabled.value) }) {
        Text("Toggle torch")
      }
    }
  }
}
