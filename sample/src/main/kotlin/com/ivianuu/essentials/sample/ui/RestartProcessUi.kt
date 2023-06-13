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
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

@Provide val restartProcessHomeItem = HomeItem("Restart process") { RestartProcessKey() }

class RestartProcessKey : Key<Unit>

@Provide fun restartProcessUi(
  processRestarter: ProcessRestarter,
  scope: ScopedCoroutineScope<KeyUiScope>
) = Ui<RestartProcessKey, Unit> { model ->
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
