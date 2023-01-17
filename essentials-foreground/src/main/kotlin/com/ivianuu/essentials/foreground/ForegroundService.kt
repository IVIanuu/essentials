/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.combine
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.time.Clock
import com.ivianuu.essentials.time.seconds
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.coroutines.MainContext
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration

class ForegroundService : Service() {
  private val component by lazy {
    application
      .cast<AppElementsOwner>()
      .appElements
      .element<ForegroundServiceComponent>()
  }

  @Provide val logger get() = component.logger

  private var previousStates = emptyList<ForegroundManagerImpl.ForegroundState>()

  private var job: Job? = null
  private var stopJob: Job? = null

  private var startTime = Duration.ZERO

  override fun onCreate() {
    super.onCreate()
    log { "start service" }

    startTime = component.clock.now()

    job = component.scope.launch(start = CoroutineStart.UNDISPATCHED) {
      guarantee(
        block = {
          component.foregroundManager.states
            .flatMapLatest { states ->
              combine(states.map { it.notification })
                .map { states }
            }
            .collect { applyState(it, false) }
        },
        finalizer = { applyState(emptyList(), true) }
      )
    }
  }

  override fun onDestroy() {
    log { "stop service" }
    job?.cancel()
    super.onDestroy()
  }

  private suspend fun applyState(
    states: List<ForegroundManagerImpl.ForegroundState>,
    fromStop: Boolean
  ) = withContext(component.mainContext) {
    log { "apply states: $states" }

    previousStates
      .filter { state -> states.none { state.id == it.id } }
      .forEach { component.notificationManager.cancel(it.id) }

    if (states.isNotEmpty()) {
      stopJob
        ?.cancel()
        ?.also { log { "cancel delayed stop" } }
      stopJob = null

      states
        .forEachIndexed { index, state ->
          if (index == 0) {
            log { "start foreground" }
            startForeground(state.id, state.notification.value)
          } else {
            component.notificationManager.notify(state.id, state.notification.value)
          }
        }
    } else if (!fromStop && stopJob?.isActive != true) {
      log { "stop foreground" }
      stopForeground(true)

      stopJob = component.scope.launch {
        log { "dispatch delayed stop" }
        delay(6.seconds)
        stopSelf()
      }
    }

    previousStates = states

    states.forEach { it.seen.complete(Unit) }
  }

  override fun onBind(intent: Intent?): IBinder? = null
}

@Provide @Element<AppScope> data class ForegroundServiceComponent(
  val clock: Clock,
  val foregroundManager: ForegroundManagerImpl,
  val notificationManager: NotificationManager,
  val logger: Logger,
  val mainContext: MainContext,
  val scope: NamedCoroutineScope<AppScope>
)
