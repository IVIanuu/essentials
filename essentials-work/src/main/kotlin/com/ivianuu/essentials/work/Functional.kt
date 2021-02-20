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

import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.toJavaDuration

typealias Worker = suspend () -> ListenableWorker.Result

interface WorkScope {
    val inputData: Data
    val id: UUID
    val tags: Set<String>
    val runAttemptCount: Int
    suspend fun setProgress(data: Data)
    suspend fun setForeground(foregroundInfo: ForegroundInfo)
}

typealias WorkerElement = Pair<WorkerId, (@Given WorkScope) -> Worker>

@Qualifier
annotation class WorkerBinding<S>

@Suppress("UNCHECKED_CAST")
@Macro
@GivenSetElement
fun <P : @WorkerBinding<I> S, S : suspend () -> ListenableWorker.Result, I : WorkerId> workerBindingImpl(
    @Given id: I,
    @Given factory: (@Given WorkScope) -> P
): WorkerElement = id to factory

fun OneTimeWorkRequestBuilder(id: WorkerId): OneTimeWorkRequest.Builder =
    OneTimeWorkRequestBuilder<FunctionalWorker>()
        .addTag(WORKER_ID_TAG_PREFIX + id.value)

fun PeriodicWorkRequestBuilder(
    id: WorkerId,
    repeatInterval: Duration,
    flexTimeInterval: Duration? = null
): PeriodicWorkRequest.Builder =
    (if (flexTimeInterval != null) PeriodicWorkRequestBuilder<FunctionalWorker>(
        repeatInterval.toJavaDuration(), flexTimeInterval.toJavaDuration()
    ) else PeriodicWorkRequestBuilder<FunctionalWorker>(repeatInterval.toJavaDuration()))
        .addTag(WORKER_ID_TAG_PREFIX + id.value)
