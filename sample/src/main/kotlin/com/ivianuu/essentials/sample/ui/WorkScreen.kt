/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.sample.SampleWorkScheduler
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide

@Provide val workHomeItem = HomeItem("Work") { WorkScreen() }

class WorkScreen : Screen<Unit>

@Provide fun workUi(scheduler: SampleWorkScheduler) = Ui<WorkScreen, Unit> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Work") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = action { scheduler() }
    ) {
      Text("Perform work")
    }
  }
}