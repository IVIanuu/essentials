/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.work.PeriodicWorkSchedule
import com.ivianuu.essentials.work.WorkId
import com.ivianuu.essentials.work.WorkManager
import com.ivianuu.essentials.work.Worker
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@Provide val workHomeItem = HomeItem("Work") { WorkScreen() }

class WorkScreen : Screen<Unit>

@Provide fun workUi(workManager: WorkManager) = Ui<WorkScreen, Unit> {
  ScreenScaffold(topBar = { AppBar { Text("Work") } }) {
    Column {
      if (workManager.isWorkerRunning(SampleWorkId).collectAsState().value)
        CircularProgressIndicator()

      Button(
        modifier = Modifier.center(),
        onClick = action { workManager.runWorker(SampleWorkId) }
      ) {
        Text("Run")
      }
    }
  }
}

@Provide object SampleWorkId : WorkId("sample")

@Provide val sampleWorkSchedule: PeriodicWorkSchedule<SampleWorkId>
  get() = PeriodicWorkSchedule(8.hours)

@Provide fun sampleWorker() = Worker<SampleWorkId> {
  delay(3.seconds)
}
