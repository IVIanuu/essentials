/*
 * Copyright 2020 Manuel Wrage
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

import androidx.work.ListenableWorker
import androidx.work.WorkManager
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.work.OneTimeWorkRequestBuilder
import com.ivianuu.essentials.work.WorkScope
import com.ivianuu.essentials.work.Worker
import com.ivianuu.essentials.work.WorkerId
import com.ivianuu.injekt.Given
import kotlinx.coroutines.delay

@Given
object TestWorkerId : WorkerId("test")

@Given
fun testWorker(@Given scope: WorkScope, @Given logger: Logger): Worker<TestWorkerId> = {
    logger.d { "start work in scope: $scope" }
    delay(5000)
    logger.d { "finish work in scope: $scope" }
    ListenableWorker.Result.success()
}

typealias TestWorkScheduler = () -> Unit

@Given
fun testWorkScheduler(@Given workManager: WorkManager): TestWorkScheduler = {
    workManager.enqueue(OneTimeWorkRequestBuilder(TestWorkerId).build())
}
