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
import com.ivianuu.essentials.time.hours
import com.ivianuu.essentials.time.seconds
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.work.IsWorkerRunning
import com.ivianuu.essentials.work.PeriodicWorkSchedule
import com.ivianuu.essentials.work.WorkId
import com.ivianuu.essentials.work.Worker
import com.ivianuu.essentials.work.WorkerRunner
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Provide val workHomeItem = HomeItem("Work") { WorkScreen() }

class WorkScreen : Screen<Unit>

@Provide fun workUi(
  isRunning: StateFlow<IsWorkerRunning<SampleWorkId>>,
  runner: WorkerRunner<SampleWorkId>
) = Ui<WorkScreen, Unit> {
  Scaffold(
    topBar = { TopAppBar(title = { Text("Work") }) }
  ) {
    Column {
      if (isRunning.collectAsState().value.value)
        CircularProgressIndicator()

      Button(
        modifier = Modifier.center(),
        onClick = action { runner() }
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
