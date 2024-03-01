/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.systemoverlay.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

@Provide val systemWindowHomeItem = HomeItem("System Window") { SystemWindowScreen() }

class SystemWindowScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      permissionManager: PermissionManager,
      scope: ScopedCoroutineScope<ScreenScope>,
      systemWindowManager: SystemWindowManager
    ) = Ui<SystemWindowScreen> {
      ScreenScaffold(topBar = { AppBar { Text("System window") } }) {
        var showSystemWindow by remember { mutableStateOf(false) }

        if (showSystemWindow)
          systemWindowManager.SystemWindow {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.Red)
                .systemWindowTrigger()
                .clickable { showSystemWindow = false }
            )
          }

        Button(
          modifier = Modifier.center(),
          onClick = {
            scope.launch {
              if (permissionManager.requestPermissions(listOf(SampleSystemOverlayPermission::class)))
                showSystemWindow = true
            }
          }
        ) {
          Text("Attach system window")
        }
      }
    }
  }
}
