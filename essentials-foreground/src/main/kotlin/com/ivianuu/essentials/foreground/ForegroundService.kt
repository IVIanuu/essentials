/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.combine
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.time.Clock
import com.ivianuu.essentials.time.seconds
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.MainCoroutineContext
import com.ivianuu.injekt.common.NamedCoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration

@Provide @AndroidComponent class ForegroundService(
  private val clock: Clock,
  private val foregroundManager: ForegroundManagerImpl,
  private val notificationManager: @SystemService NotificationManager,
  private val logger: Logger,
  private val mainCoroutineContext: MainCoroutineContext,
  private val scope: NamedCoroutineScope<AppScope>
) : Service() {
  private var previousStates = emptyList<ForegroundManagerImpl.ForegroundState>()

  private var job: Job? = null
  private var stopJob: Job? = null

  private var startTime = Duration.ZERO

  override fun onCreate() {
    super.onCreate()
    logger.log { "start service" }
    startTime = clock()

    job = scope.launch(start = CoroutineStart.UNDISPATCHED) {
      guarantee(
        block = {
          foregroundManager.states
            .flatMapLatest { states ->
              if (states.isEmpty()) flowOf(emptyList())
              else combine(
                states
                  .map { it.notification }
              ).map { states }
            }
            .collect { applyState(it, false) }
        },
        finalizer = { applyState(emptyList(), true) }
      )
    }
  }

  override fun onDestroy() {
    logger.log { "stop service" }
    job?.cancel()
    super.onDestroy()
  }

  private suspend fun applyState(
    states: List<ForegroundManagerImpl.ForegroundState>,
    fromStop: Boolean
  ) = withContext(mainCoroutineContext) {
    logger.log { "apply states: $states" }

    previousStates
      .filter { state -> states.none { state.id == it.id } }
      .forEach { notificationManager.cancel(it.id) }

    if (states.isNotEmpty()) {
      stopJob
        ?.cancel()
        ?.also { logger.log { "cancel delayed stop" } }
      stopJob = null

      states
        .forEachIndexed { index, state ->
          if (index == 0) {
            logger.log { "start foreground" }
            startForeground(state.id, state.notification.value)
          } else {
            notificationManager.notify(state.id, state.notification.value)
          }
        }
    } else if (!fromStop && stopJob?.isActive != true) {
      logger.log { "stop foreground" }
      stopForeground(true)

      stopJob = scope.launch {
        logger.log { "dispatch delayed stop" }
        delay(6.seconds)
        stopSelf()
      }
    }

    previousStates = states

    states.forEach { it.seen.complete(Unit) }
  }

  override fun onBind(intent: Intent?): IBinder? = null
}
