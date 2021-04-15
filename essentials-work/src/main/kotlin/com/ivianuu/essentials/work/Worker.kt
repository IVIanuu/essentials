package com.ivianuu.essentials.work

import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import com.ivianuu.injekt.Given
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

@Given
fun <@Given T : Worker<I>, I : WorkerId> workerElement(
    @Given id: I,
    @Given factory: () -> T
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
