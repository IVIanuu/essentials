/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import androidx.compose.ui.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.processrestart.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*

@Provide val restartProcessHomeItem = HomeItem("Restart process") { RestartProcessKey }

object RestartProcessKey : Key<Unit>

@Provide fun restartProcessUi(
  processRestarter: ProcessRestarter,
  scope: NamedCoroutineScope<KeyUiScope>
) = KeyUi<RestartProcessKey> {
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
