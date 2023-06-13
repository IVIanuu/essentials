/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide

@Provide val drawerHomeItem = HomeItem("Drawer") { DrawerScreen() }

class DrawerScreen : Screen<Unit>

@Provide val drawerUi = Ui<DrawerScreen, Unit> { model ->
  Scaffold(
    topBar = { TopAppBar(title = { Text("Drawer") }) },
    drawerContent = {
      Surface(color = Color.Blue) {
        Text(
          text = "Drawer",
          style = MaterialTheme.typography.h4,
          modifier = Modifier.center()
        )
      }
    }
  ) {
    Surface(color = Color.Red) {
      Text(
        text = "Body",
        style = MaterialTheme.typography.h4,
        modifier = Modifier.center()
      )
    }
  }
}
