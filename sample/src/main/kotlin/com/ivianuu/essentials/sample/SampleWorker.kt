/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.work.InjektWorker
import kotlinx.coroutines.delay

@Provide @InjektWorker class SampleWorker(
  private val appContext: AppContext,
  private val logger: Logger,
  private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    logger.log { "start work" }
    delay(5000)
    logger.log { "finish work" }
    return Result.success()
  }
}

fun interface SampleWorkScheduler {
  suspend operator fun invoke()
}

@Provide fun sampleWorkScheduler(workManager: WorkManager) = SampleWorkScheduler {
  workManager.enqueue(OneTimeWorkRequestBuilder<SampleWorker>().build())
}
