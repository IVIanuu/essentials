/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.torch.Torch
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

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
