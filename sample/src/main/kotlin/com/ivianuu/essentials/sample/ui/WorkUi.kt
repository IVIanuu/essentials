/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import androidx.compose.ui.*
import com.ivianuu.essentials.sample.work.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide val workHomeItem = HomeItem("Work") { WorkKey }

object WorkKey : Key<Unit>

@Provide fun workUi(testWorkScheduler: TestWorkScheduler) = SimpleKeyUi<WorkKey> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Work") }) }
  ) {
    Button(
      modifier = Modifier.center(),
      onClick = { testWorkScheduler() }
    ) {
      Text("Perform work")
    }
  }
}
