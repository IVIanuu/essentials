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
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.work.WorkerBinding
import com.ivianuu.essentials.work.worker
import kotlinx.coroutines.delay

const val TEST_WORKER_ID = "test"

@WorkerBinding
fun TestWorker(logger: Logger) = worker(TEST_WORKER_ID) {
    logger.d("start work")
    delay(5000)
    logger.d("finish work")
    ListenableWorker.Result.success()
}
