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
import essentials.compose.*
import essentials.permission.*
import essentials.systemoverlay.*
import essentials.ui.material.*
import essentials.ui.overlay.*
import injekt.*

@Provide fun systemWindowHomeItem(
  permissions: Permissions,
  systemWindows: SystemWindows
) = HomeItem("System Window") {
  BottomSheetScreen {
    Subheader { Text("System Window") }

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

    SectionListItem(
      sectionType = SectionType.SINGLE,
      onClick = action {
        if (permissions.ensurePermissions(listOf(SampleSystemOverlayPermission::class)))
          showSystemWindow = true
      },
      title = { Text("Attach system window") }
    )
  }
}
