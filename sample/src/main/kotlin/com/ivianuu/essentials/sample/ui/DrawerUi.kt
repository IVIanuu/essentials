/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide val drawerHomeItem = HomeItem("Drawer") { DrawerKey }

object DrawerKey : Key<Unit>

@Provide val drawerUi = KeyUi<DrawerKey> {
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
