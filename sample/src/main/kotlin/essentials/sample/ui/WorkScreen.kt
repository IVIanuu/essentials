/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.work.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@Provide val workHomeItem = HomeItem("Work") { WorkScreen() }

class WorkScreen : Screen<Unit>

@Provide @Composable fun WorkUi(workManager: WorkManager): Ui<WorkScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Work") } }) {
    Column {
      if (workManager.isWorkerRunning(SampleWorkId))
        CircularProgressIndicator()

      Button(
        modifier = Modifier.fillMaxSize().wrapContentSize(),
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

@Provide suspend fun doSampleWork(): WorkerResult<SampleWorkId> {
  delay(3.seconds)
  return WorkerResult.success()
}
