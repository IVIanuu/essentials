/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import essentials.coroutines.*
import essentials.permission.*
import essentials.systemoverlay.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

@Provide val systemWindowHomeItem = HomeItem("System Window") { SystemWindowScreen() }

class SystemWindowScreen : Screen<Unit>

@Provide @Composable fun SystemWindowUi(
  permissions: Permissions,
  context: ScreenContext<SystemWindowScreen> = inject,
  systemWindows: SystemWindows
): Ui<SystemWindowScreen> {
  EsScaffold(topBar = { EsAppBar { Text("System window") } }) {
    var showSystemWindow by remember { mutableStateOf(false) }

    if (showSystemWindow)
      systemWindows.SystemWindow {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
            .systemWindowTrigger()
            .clickable { showSystemWindow = false }
        )
      }

    Button(
      modifier = Modifier.fillMaxSize().wrapContentSize(),
      onClick = {
        launch {
          if (permissions.ensurePermissions(listOf(SampleSystemOverlayPermission::class)))
            showSystemWindow = true
        }
      }
    ) {
      Text("Attach system window")
    }
  }
}
