/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.work

import android.content.Context
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.await
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.app.ScopeInitializer
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.ExitCase
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.fold
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Serializable abstract class WorkId(val value: String)

@Serializable data class PeriodicWorkSchedule<I : WorkId>(
  val interval: Duration,
  val constraints: WorkConstraints = WorkConstraints()
)

@Serializable data class WorkConstraints(
  val networkType: NetworkType = NetworkType.ANY,
  val requiresCharging: Boolean = false
) {
  enum class NetworkType { ANY, CONNECTED, UNMETERED }
}

fun interface Worker<I : WorkId> {
  suspend operator fun invoke()
}

@JvmInline value class IsWorkerRunning<I : WorkId>(val value: Boolean)

fun interface WorkerRunner<I : WorkId> {
  suspend operator fun invoke()
}

@Provide fun <I : WorkId> workerRunner(
  coroutineContexts: CoroutineContexts,
  logger: Logger,
  isWorkerRunning: MutableStateFlow<IsWorkerRunning<I>>,
  scope: ScopedCoroutineScope<AppScope>,
  worker: () -> Worker<I>,
  workId: I
) = WorkerRunner<I> {
  withContext(scope.coroutineContext + coroutineContexts.computation) {
    logger.log { "run worker ${workId.value}" }
    guarantee(
      block = {
        isWorkerRunning.value = IsWorkerRunning(true)
        worker()()
      },
      finalizer = {
        if (it is ExitCase.Failure) it.failure.printStackTrace()
        isWorkerRunning.value = IsWorkerRunning(false)
        logger.log { "run worker end ${workId.value}" }
      }
    )
  }
}

object WorkModule {
  @Provide fun <@Spread T : WorkId> workerRunner(
    id: T,
    runner: () -> WorkerRunner<T>,
  ): Pair<String, () -> WorkerRunner<*>> = id.value to runner

  @Provide val defaultWorkerRunners get() = emptyList<Pair<String, () -> WorkerRunner<*>>>()

  @Provide fun <@Spread T : WorkId> workSchedule(
    id: T,
    schedule: PeriodicWorkSchedule<T>,
  ): Pair<String, PeriodicWorkSchedule<*>> = id.value to schedule

  @Provide fun <@Spread T : WorkId> isWorkerRunning(id: T):
      @Scoped<AppScope> MutableStateFlow<IsWorkerRunning<T>> =
    MutableStateFlow(IsWorkerRunning(false))

  @Provide val defaultSchedules get() = emptyList<Pair<String, PeriodicWorkSchedule<*>>>()

  @Provide fun workManager(context: AppContext) = WorkManager.getInstance(context)
}

@Provide class EsWorker(
  appContext: Context,
  params: WorkerParameters,
  private val logger: Logger,
  private val workerRunners: Map<String, () -> WorkerRunner<*>>,
  private val workManager: WorkManager
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    val workId = tags.single { it.startsWith(WORK_ID_PREFIX) }.removePrefix(WORK_ID_PREFIX)

    val runner = workerRunners[workId]?.invoke() ?: kotlin.run {
      logger.log { "no worker found for $workId" }
      workManager.cancelAllWorkByTag(WORK_ID_PREFIX + workId)
      return Result.success()
    }

    return catch { runner() }.fold({ Result.success() }, { Result.retry() })
  }
}

@Provide class EsWorkerFactory(
  private val worker: (AppContext, WorkerParameters) -> EsWorker
) : WorkerFactory() {
  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters
  ): ListenableWorker? =
    if (workerClassName == EsWorker::class.java.name) worker(appContext, workerParameters)
    else null
}

fun interface WorkInitializer : ScopeInitializer<AppScope>

@Provide fun workInitializer(appContext: AppContext, workerFactory: WorkerFactory) = WorkInitializer {
  WorkManager.initialize(
    appContext,
    Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()
  )
}

@Provide fun periodicWorkScheduler(
  coroutineContexts: CoroutineContexts,
  json: Json,
  logger: Logger,
  schedules: Map<String, PeriodicWorkSchedule<*>>,
  workManager: WorkManager,
) = ScopeWorker<AppScope> {
  withContext(coroutineContexts.computation) {
    schedules.forEach { (workId, schedule) ->
      val serializedSchedule = json.encodeToString(serializer<PeriodicWorkSchedule<WorkId>>(), schedule.cast())
      val existingWork = workManager.getWorkInfosByTag(WORK_ID_PREFIX + workId).await()
      if (existingWork.none {
          (it.state == WorkInfo.State.RUNNING ||
              it.state == WorkInfo.State.ENQUEUED) &&
              it.tags.singleOrNull { it.startsWith(WORK_SCHEDULE_PREFIX) }
                ?.removePrefix(WORK_SCHEDULE_PREFIX) == serializedSchedule
        }) {
        logger.log { "enqueue periodic work $workId -> $schedule" }

        workManager.cancelAllWorkByTag(WORK_ID_PREFIX + workId)
        workManager.enqueue(
          PeriodicWorkRequestBuilder<EsWorker>(schedule.interval.toJavaDuration())
            .addTag(WORK_ID_PREFIX + workId)
            .addTag(WORK_SCHEDULE_PREFIX + serializedSchedule)
            .setConstraints(
              Constraints.Builder()
                .setRequiresCharging(schedule.constraints.requiresCharging)
                .setRequiredNetworkType(
                  when (schedule.constraints.networkType) {
                    WorkConstraints.NetworkType.ANY -> NetworkType.NOT_REQUIRED
                    WorkConstraints.NetworkType.CONNECTED -> NetworkType.CONNECTED
                    WorkConstraints.NetworkType.UNMETERED -> NetworkType.UNMETERED
                  }
                )
                .build()
            )
            .build()
        )
      } else {
        logger.log { "do not enqueue unchanged periodic work $workId -> $schedule" }
      }
    }
  }
}

private const val WORK_ID_PREFIX = "work_id_"
private const val WORK_SCHEDULE_PREFIX = "work_schedule_"
