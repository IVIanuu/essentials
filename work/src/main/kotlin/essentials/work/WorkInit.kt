/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.work

import android.annotation.*
import android.content.*
import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import androidx.work.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.coroutines.*
import essentials.logging.*
import essentials.logging.Logger
import injekt.*
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.coroutines.*
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

@Tag typealias WorkerResult<I> = ListenableWorker.Result

@Provide @Scoped<AppScope> class WorkManager(
  private val androidWorkManager: AndroidWorkManager,
  private val coroutineContexts: CoroutineContexts,
  @property:Provide private val logger: Logger,
  @property:Provide private val scope: ScopedCoroutineScope<AppScope>,
  private val workersMap: Map<String, suspend () -> WorkerResult<*>>,
) : SynchronizedObject() {
  private val workerStates = mutableMapOf<String, MutableState<Boolean>>()
  private val sharedWorkers = sharedComputation<WorkId, WorkerResult<*>> { id ->
    d { "run worker ${id.value}" }

    var workerState by synchronized(this@WorkManager) {
      workerStates.getOrPut(id.value) { mutableStateOf(false) }
    }

    guaranteeCase(
      fa = {
        workerState = true
        workersMap[id.value]!!.invoke()
      },
      finalizer = {
        if (it is ExitCase.Failure) it.failure.printStackTrace()
        workerState = false
        d { "run worker end ${id.value}" }
      }
    )
  }

  fun <I : WorkId> isWorkerRunning(id: I) = synchronized(this) {
    workerStates.getOrPut(id.value) { mutableStateOf(false) }
  }.value

  suspend fun <I : WorkId> runWorker(id: I): WorkerResult<I> =
    withContext(scope.coroutineContext + coroutineContexts.computation) {
      if (id.value !in workersMap) {
        d { "no worker found for ${id.value}" }
        androidWorkManager.cancelUniqueWork(id.value)
        return@withContext WorkerResult.failure()
      }

      sharedWorkers(id)
    }
}

@Provide object WorkProviders {
  @Provide fun <@AddOn I : WorkId> worker(
    id: I,
    worker: suspend () -> WorkerResult<I>,
  ): Pair<String, suspend () -> WorkerResult<*>> = id.value to worker

  @Provide val defaultWorkers get() = emptyList<Pair<String, suspend () -> WorkerResult<*>>>()

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
    return workManager.runWorker(object : WorkId(workId) {})
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

data object WorkInit

@Provide fun initializeWork(
  appContext: AppContext,
  workerFactory: WorkerFactory
): ScopeInit<AppScope, WorkInit> {
  AndroidWorkManager.initialize(
    appContext,
    Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()
  )
}

@SuppressLint("RestrictedApi")
@Provide @Composable fun PeriodicWorkScheduler(
  coroutineContexts: CoroutineContexts,
  logger: Logger = inject,
  schedules: Map<String, PeriodicWorkSchedule<*>>,
  androidWorkManager: androidx.work.WorkManager,
): ScopeContent<AppScope> {
  LaunchedEffect(true) {
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
          d { "enqueue work $workId with $schedule" }

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
          d { "do not reenqueue work $workId with $schedule" }
        }
      }
    }
  }
}

private const val WORK_ID = "work_id"
private const val SCHEDULE_HASH_PREFIX = "schedule_hash_"
