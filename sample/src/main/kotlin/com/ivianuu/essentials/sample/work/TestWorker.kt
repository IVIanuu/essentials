/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.work

import androidx.work.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.work.*
import kotlinx.coroutines.*

@Provide @InjektWorker class TestWorker(
  appContext: AppContext,
  params: WorkerParameters,
  private val L: Logger
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    log { "start work" }
    delay(5000)
    log { "finish work" }
    return Result.success()
  }
}

fun interface TestWorkScheduler : () -> Unit

@Provide fun testWorkScheduler(workManager: WorkManager) = TestWorkScheduler {
  workManager.enqueue(OneTimeWorkRequestBuilder<TestWorker>().build())
}
