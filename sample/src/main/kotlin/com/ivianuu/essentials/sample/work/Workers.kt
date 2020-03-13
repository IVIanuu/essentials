/*
 * Copyright 2019 Manuel Wrage
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

import android.content.Context
import androidx.work.WorkerParameters
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.work.EsWorker
import com.ivianuu.essentials.work.bindWorkerIntoMap
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import kotlinx.coroutines.delay

@Factory
class TestWorker(
    @Param context: Context,
    @Param workerParams: WorkerParameters,
    private val logger: Logger
) : EsWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        logger.d("start work")
        delay(5000)
        logger.d("finish work")
        return Result.success()
    }
}

fun ComponentBuilder.workBindings() {
    bindWorkerIntoMap<TestWorker>()
}
