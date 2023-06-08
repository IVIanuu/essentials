/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.systemoverlay.SystemWindowManager
import com.ivianuu.essentials.systemoverlay.systemWindowTrigger
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Provide val systemWindowHomeItem = HomeItem("System Window") { SystemWindowKey() }

class SystemWindowKey : Key<Unit>

@Provide fun systemWindowUi(
  ctx: KeyUiContext<SystemWindowKey>,
  permissionManager: PermissionManager,
  systemWindowManager: SystemWindowManager
) = KeyUi<SystemWindowKey, Unit> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("System window") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = {
        lateinit var job: Job
        job = ctx.launch {
          if (permissionManager.requestPermissions(listOf(typeKeyOf<SampleSystemOverlayPermission>()))) {
            systemWindowManager.attachSystemWindow {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color.Red)
                  .systemWindowTrigger()
                  .clickable { job.cancel() }
              )
            }
          }
        }
      }
    ) {
      Text("Attach system window")
    }
  }
}
