/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.systemoverlay.SystemWindowManager
import com.ivianuu.essentials.systemoverlay.systemWindowTrigger
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.ScreenScope
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.launch

@Provide val systemWindowHomeItem = HomeItem("System Window") { SystemWindowScreen() }

class SystemWindowScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      permissionManager: PermissionManager,
      scope: ScopedCoroutineScope<ScreenScope>,
      systemWindowManager: SystemWindowManager
    ) = Ui<SystemWindowScreen, Unit> {
      ScreenScaffold(topBar = { AppBar { Text("System window") } }) {
        var showSystemWindow by remember { mutableStateOf(false) }

        if (showSystemWindow)
          LaunchedEffect(true) {
            systemWindowManager.attachSystemWindow {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color.Red)
                  .systemWindowTrigger()
                  .clickable { showSystemWindow = false }
              )
            }
          }

        Button(
          modifier = Modifier.center(),
          onClick = {
            scope.launch {
              if (permissionManager.requestPermissions(listOf(typeKeyOf<SampleSystemOverlayPermission>())))
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
