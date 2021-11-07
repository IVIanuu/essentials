/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.work

import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.logging.Log
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.android.work.InjektWorker
import kotlinx.coroutines.delay

@Provide @InjektWorker @Log class TestWorker(
  appContext: AppContext,
  params: WorkerParameters
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    log { "start work" }
    delay(5000)
    log { "finish work" }
    return Result.success()
  }
}

@Tag annotation class TestWorkSchedulerTag
typealias TestWorkScheduler = @TestWorkSchedulerTag () -> Unit

@Provide fun testWorkScheduler(workManager: WorkManager): TestWorkScheduler = {
  workManager.enqueue(OneTimeWorkRequestBuilder<TestWorker>().build())
}
