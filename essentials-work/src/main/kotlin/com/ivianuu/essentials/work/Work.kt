/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.work

import android.content.Context
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.app.ScopeInitializer
import com.ivianuu.essentials.app.ScopeWorker
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
import kotlin.time.Duration
import kotlin.time.toJavaDuration
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
  private val workers: Map<String, () -> Worker<*>>,
) : WorkManager, SynchronizedObject() {
  private val workerStates = mutableMapOf<String, MutableStateFlow<Boolean>>()

  override fun <I : WorkId> isWorkerRunning(id: I) = synchronized(this) {
    workerStates.getOrPut(id.value) { MutableStateFlow(false) }
  }

  override suspend fun <I : WorkId> runWorker(id: I): Unit =
    withContext(scope.coroutineContext + coroutineContexts.computation) {
      if (id.value !in workers) {
        logger.log { "no worker found for ${id.value}" }
        androidWorkManager.cancelUniqueWork(id.value)
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
}

@Provide class EsWorker(
  appContext: Context,
  params: WorkerParameters,
  private val workManager: WorkManager
) : CoroutineWorker(appContext, params) {
  override suspend fun doWork(): Result {
    val workId = inputData.getString(WORK_ID) ?: return Result.failure()
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
  schedules: Map<String, PeriodicWorkSchedule<*>>,
  androidWorkManager: AndroidWorkManager,
) = ScopeWorker<AppScope> {
  withContext(coroutineContexts.computation) {
    schedules.forEach { (workId, schedule) ->
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
          .build()
      )
    }
  }
}

private const val WORK_ID = "work_id"
