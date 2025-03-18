/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.animation.*
import androidx.compose.material3.*
import essentials.compose.*
import essentials.ui.material.*
import essentials.ui.overlay.*
import essentials.work.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@Provide fun workHomeItem(workManager: WorkManager) = HomeItem("Work") {
  BottomSheetScreen {
    Subheader { Text("Work") }
    SectionListItem(
      sectionType = SectionType.SINGLE,
      onClick = action { workManager.runWorker(SampleWorkId) },
      title = { Text("Run") },
      trailing = {
        AnimatedVisibility(workManager.isWorkerRunning(SampleWorkId)) {
          CircularProgressIndicator()
        }
      }
    )
  }
}

@Provide object SampleWorkId : WorkId("sample")

@Provide val sampleWorkSchedule: PeriodicWorkSchedule<SampleWorkId>
  get() = PeriodicWorkSchedule(8.hours)

@Provide suspend fun doSampleWork(): WorkerResult<SampleWorkId> {
  delay(3.seconds)
  return WorkerResult.success()
}
