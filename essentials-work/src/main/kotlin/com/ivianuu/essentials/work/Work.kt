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
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import androidx.work.WorkManager as AndroidWorkManager

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

interface WorkManager {
  fun <I : WorkId> isWorkerRunning(id: I): StateFlow<Boolean>

  suspend fun <I : WorkId> runWorker(id: I)
}

@Provide @Scoped<AppScope> class WorkManagerImpl(
  private val androidWorkManager: AndroidWorkManager,
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val workIds: List<WorkId>,
  private val workers: Map<String, () -> Worker<*>>,
) : WorkManager, SynchronizedObject() {
  private val workerStates = mutableMapOf<String, MutableStateFlow<Boolean>>()

  override fun <I : WorkId> isWorkerRunning(id: I) = synchronized(this) {
    workerStates.getOrPut(id.value) { MutableStateFlow(false) }
  }

  override suspend fun <I : WorkId> runWorker(id: I): Unit =
    withContext(scope.coroutineContext + coroutineContexts.computation) {
      if (id.value !in workIds.map { it.value }) {
        logger.log { "no worker found for ${id.value}" }
        androidWorkManager.cancelAllWorkByTag(WORK_ID_PREFIX + id.value)
        return@withContext
      }

      logger.log { "run worker ${id.value}" }
      val workerState = synchronized(this@WorkManagerImpl) {
        workerStates.getOrPut(id.value) { MutableStateFlow(false) }
      }
      guarantee(
        block = {
          workerState.value = true
          workers[id.value]!!.invoke().invoke()
        },
        finalizer = {
          if (it is ExitCase.Failure) it.failure.printStackTrace()
          workerState.value = false
          logger.log { "run worker end ${id.value}" }
        }
      )
    }
}

object WorkModule {
  @Provide fun <@Spread I : WorkId> worker(
    id: I,
    worker: () -> Worker<I>,
  ): Pair<String, () -> Worker<*>> = id.value to worker

  @Provide val defaultWorkers get() = emptyList<Pair<String, () -> Worker<*>>>()

  @Provide fun <@Spread I : WorkId> workSchedule(
    id: I,
    schedule: PeriodicWorkSchedule<I>,
  ): Pair<String, PeriodicWorkSchedule<*>> = id.value to schedule

  @Provide val defaultSchedules get() = emptyList<Pair<String, PeriodicWorkSchedule<*>>>()

  @Provide fun androidWorkManager(context: AppContext) = AndroidWorkManager.getInstance(context)

  @Provide val defaultWorkIds get() = emptyList<WorkId>()
}

@Provide class EsWorker(
  appContext: Context,
  params: WorkerParameters,
  private val workManager: WorkManager
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    val workId = tags.single { it.startsWith(WORK_ID_PREFIX) }.removePrefix(WORK_ID_PREFIX)
    return catch {
      workManager.runWorker(object : WorkId(workId) {})
    }.fold({ Result.success() }, { Result.retry() })
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
  AndroidWorkManager.initialize(
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
  androidWorkManager: AndroidWorkManager,
) = ScopeWorker<AppScope> {
  withContext(coroutineContexts.computation) {
    schedules.forEach { (workId, schedule) ->
      val serializedSchedule = json.encodeToString(serializer<PeriodicWorkSchedule<WorkId>>(), schedule.cast())
      val existingWork = androidWorkManager.getWorkInfosByTag(WORK_ID_PREFIX + workId).await()
      if (existingWork.none {
          (it.state == WorkInfo.State.RUNNING ||
              it.state == WorkInfo.State.ENQUEUED) &&
              it.tags.singleOrNull { it.startsWith(WORK_SCHEDULE_PREFIX) }
                ?.removePrefix(WORK_SCHEDULE_PREFIX) == serializedSchedule
        }) {
        logger.log { "enqueue periodic work $workId -> $schedule" }

        androidWorkManager.cancelAllWorkByTag(WORK_ID_PREFIX + workId)

        androidWorkManager.enqueue(
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
