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

package com.ivianuu.essentials.work

import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.toJavaDuration

typealias Worker<I> = suspend WorkScope.() -> ListenableWorker.Result

interface WorkScope {
  val inputData: Data
  val id: UUID
  val tags: Set<String>
  val runAttemptCount: Int
  suspend fun setProgress(data: Data)
  suspend fun setForeground(foregroundInfo: ForegroundInfo)
}

abstract class WorkerId(val value: String)

typealias WorkerElement = Pair<WorkerId, () -> Worker<*>>

@Provide fun <@Spread T : Worker<I>, I : WorkerId> workerElement(
  id: I,
  factory: () -> T
): WorkerElement = id to factory

fun WorkerId.toFunctionalWorkerTag() = WORKER_ID_TAG_PREFIX + value

fun OneTimeWorkRequestBuilder(id: WorkerId): OneTimeWorkRequest.Builder =
  OneTimeWorkRequestBuilder<FunctionalWorker>()
    .addTag(id.toFunctionalWorkerTag())

fun PeriodicWorkRequestBuilder(
  id: WorkerId,
  repeatInterval: Duration,
  flexTimeInterval: Duration? = null
): PeriodicWorkRequest.Builder =
  (if (flexTimeInterval != null) PeriodicWorkRequestBuilder<FunctionalWorker>(
    repeatInterval.toJavaDuration(), flexTimeInterval.toJavaDuration()
  ) else PeriodicWorkRequestBuilder<FunctionalWorker>(repeatInterval.toJavaDuration()))
    .addTag(id.toFunctionalWorkerTag())

internal const val WORKER_ID_TAG_PREFIX = "worker_id_"
