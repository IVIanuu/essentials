/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.work.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@Provide val workHomeItem = HomeItem("Work") { WorkScreen() }

class WorkScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(workManager: WorkManager) = Ui<WorkScreen, Unit> {
      ScreenScaffold(topBar = { AppBar { Text("Work") } }) {
        Column {
          if (workManager.isWorkerRunning(SampleWorkId).collect())
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
  }
}

@Provide object SampleWorkId : WorkId("sample")

@Provide val sampleWorkSchedule: PeriodicWorkSchedule<SampleWorkId>
  get() = PeriodicWorkSchedule(8.hours)

@Provide fun sampleWorker() = Worker<SampleWorkId> {
  delay(3.seconds)
}
