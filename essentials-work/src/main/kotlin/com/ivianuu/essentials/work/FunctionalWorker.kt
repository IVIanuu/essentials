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

package com.ivianuu.essentials.work

import android.content.Context
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenGroup
import com.ivianuu.injekt.android.work.worker
import java.util.UUID

@GivenGroup val functionalWorkerBinding = worker<FunctionalWorker>()

@Given class FunctionalWorker(
    @Given private val workers: Workers,
    @Given context: Context,
    @Given workerParams: WorkerParameters,
) : EsWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val id = tags.first { it.startsWith(WORKER_ID_TAG_PREFIX) }
            .removePrefix(WORKER_ID_TAG_PREFIX)
        val worker = workers[id]?.invoke() ?: error("No worker found for $id")
        val scope = object : WorkScope {
            override val id: UUID
                get() = this@FunctionalWorker.id
            override val inputData: Data
                get() = this@FunctionalWorker.inputData
            override val runAttemptCount: Int
                get() = this@FunctionalWorker.runAttemptCount
            override val tags: Set<String>
                get() = this@FunctionalWorker.tags
            override suspend fun setForeground(foregroundInfo: ForegroundInfo) {
                this@FunctionalWorker.setForeground(foregroundInfo)
            }
            override suspend fun setProgress(data: Data) {
                this@FunctionalWorker.setProgress(data)
            }
        }

        return worker(scope)
    }
}

internal const val WORKER_ID_TAG_PREFIX = "worker_id_"

