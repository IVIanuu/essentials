/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.work

import android.annotation.*
import android.content.*
import androidx.compose.runtime.*
import androidx.compose.ui.util.fastAny
import androidx.work.*
import arrow.fx.coroutines.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.logging.Logger
import injekt.*
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.synchronized
import kotlin.time.*
import androidx.work.WorkManager as AndroidWorkManager

abstract class WorkId(val value: String)

data class PeriodicWorkSchedule<I : WorkId>(
  val interval: Duration,
  val constraints: WorkConstraints = WorkConstraints()
)

data class WorkConstraints(
  val networkType: NetworkType = NetworkType.ANY,
  val requiresCharging: Boolean = false
) {
  enum class NetworkType { ANY, CONNECTED, UNMETERED }
}

fun interface Worker<I : WorkId> {
  suspend fun doWork()
}

@Provide @Scoped<AppScope> class WorkManager(
  private val androidWorkManager: AndroidWorkManager,
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val workersMap: Map<String, () -> Worker<*>>,
) : SynchronizedObject() {
  private val workerStates = mutableMapOf<String, MutableState<Boolean>>()
  private val sharedWorkers = scope.sharedComputation<WorkId, Unit> { id ->
    logger.d { "run worker ${id.value}" }

    var workerState by synchronized(this@WorkManager) {
      workerStates.getOrPut(id.value) { mutableStateOf(false) }
    }

    guaranteeCase(
      fa = {
        workerState = true
        workersMap[id.value]!!.invoke().doWork()
      },
      finalizer = {
        if (it is ExitCase.Failure) it.failure.printStackTrace()
        workerState = false
        logger.d { "run worker end ${id.value}" }
      }
    )
  }

  fun <I : WorkId> isWorkerRunning(id: I) = synchronized(this) {
    workerStates.getOrPut(id.value) { mutableStateOf(false) }
  }.value

  suspend fun <I : WorkId> runWorker(id: I): Unit =
    withContext(scope.coroutineContext + coroutineContexts.computation) {
      if (id.value !in workersMap) {
        logger.d { "no worker found for ${id.value}" }
        androidWorkManager.cancelUniqueWork(id.value)
        return@withContext
      }

      sharedWorkers(id)
    }
}

@Provide object WorkModule {
  @Provide fun <@AddOn I : WorkId> worker(
    id: I,
    worker: () -> Worker<I>,
  ): Pair<String, () -> Worker<*>> = id.value to worker

  @Provide val defaultWorkers get() = emptyList<Pair<String, () -> Worker<*>>>()

  @Provide fun <@AddOn I : WorkId> workSchedule(
    id: I,
    schedule: PeriodicWorkSchedule<I>,
  ): Pair<String, PeriodicWorkSchedule<*>> = id.value to schedule

  @Provide val defaultSchedules get() = emptyList<Pair<String, PeriodicWorkSchedule<*>>>()

  @Provide fun androidWorkManager(appContext: AppContext) = AndroidWorkManager.getInstance(appContext)
}

@Provide class EsWorker(
  appContext: Context,
  params: WorkerParameters,
  private val workManager: WorkManager
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    val workId = inputData.getString(WORK_ID) ?: return Result.failure()
    return catch { workManager.runWorker(object : WorkId(workId) {}) }
      .printErrors()
      .fold(ifLeft = { Result.retry() }, ifRight = { Result.success() })
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

@Provide class WorkInitializer(
  private val appContext: AppContext,
  private val workerFactory: WorkerFactory
) : ScopeInitializer<AppScope> {
  override fun initialize() {
    AndroidWorkManager.initialize(
      appContext,
      Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
    )
  }
}

@SuppressLint("RestrictedApi")
@Provide fun periodicWorkScheduler(
  coroutineContexts: CoroutineContexts,
  logger: Logger,
  schedules: Map<String, PeriodicWorkSchedule<*>>,
  androidWorkManager: androidx.work.WorkManager,
) = ScopeWorker<AppScope> {
  withContext(coroutineContexts.computation) {
    schedules.forEach { (workId, schedule) ->
      val existingWork = androidWorkManager.getWorkInfosForUniqueWork(
        workId
      ).await()

      val scheduleHash = SCHEDULE_HASH_PREFIX + schedule.toString().hashCode()

      if (!existingWork.fastAny { existing ->
          (existing.state == WorkInfo.State.ENQUEUED ||
              existing.state == WorkInfo.State.RUNNING) &&
              existing.tags.any { it == scheduleHash }
        }) {
        logger.d { "enqueue work $workId with $schedule" }

        androidWorkManager.enqueueUniquePeriodicWork(
          workId,
          ExistingPeriodicWorkPolicy.UPDATE,
          PeriodicWorkRequestBuilder<EsWorker>(schedule.interval.toJavaDuration())
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
            .setInputData(workDataOf(WORK_ID to workId))
            .addTag(scheduleHash)
            .build()
        )
      } else {
        logger.d { "do not reenqueue work $workId with $schedule" }
      }
    }
  }
}

private const val WORK_ID = "work_id"
private const val SCHEDULE_HASH_PREFIX = "schedule_hash_"
