/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.torch.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import kotlinx.coroutines.*

@Provide val torchHomeItem = HomeItem("Torch") { TorchKey }

object TorchKey : Key<Unit>

@Provide fun torchUi(torch: Torch) = KeyUi<TorchKey> {
  Scaffold(topBar = { TopAppBar(title = { Text("Torch") }) }) {
    Column(
      modifier = Modifier.center(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        "Torch is ${if (torch.torchEnabled.collectAsState().value) "enabled" else "disabled"}",
        style = MaterialTheme.typography.h4
      )
      Spacer(Modifier.height(8.dp))

      val scope = rememberCoroutineScope()
      Button(
        onClick = {
          scope.launch {
            torch.setTorchState(!torch.torchEnabled.value)
          }
        }
      ) {
        Text("Toggle torch")
      }
    }
  }
}
