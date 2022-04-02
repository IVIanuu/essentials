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
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ForegroundService : Service() {
  private val component by lazy {
    application
      .let { it as AppElementsOwner }
      .appElements<ForegroundServiceComponent>()
  }

  @Provide val logger get() = component.logger

  private var previousStates = emptyList<ForegroundManagerImpl.ForegroundState>()

  private var job: Job? = null

  override fun onCreate() {
    super.onCreate()
    log { "start foreground service" }

    job = component.scope.launch(start = CoroutineStart.UNDISPATCHED) {
      guarantee(
        block = {
          component.foregroundManager.states
            .flatMapLatest { states ->
              if (states.isEmpty()) flowOf(emptyList())
              else combine(
                states
                  .map { it.notification }
              ) { states }
            }
            .collect { applyState(it) }
        },
        finalizer = { applyState(emptyList()) }
      )
    }
  }

  override fun onDestroy() {
    log { "stop foreground service" }
    job?.cancel()
    super.onDestroy()
  }

  private fun applyState(states: List<ForegroundManagerImpl.ForegroundState>) {
    log { "apply states: $states" }

    previousStates
      .filter { state -> states.none { state.id == it.id } }
      .forEach { component.notificationManager.cancel(it.id) }

    if (states.isNotEmpty()) {
      states
        .forEachIndexed { index, state ->
          if (index == 0) {
            startForeground(state.id, state.notification.value)
          } else {
            component.notificationManager.notify(state.id, state.notification.value)
          }
        }
    } else {
      stopForeground(true)
      stopSelf()
    }

    previousStates = states

    states.forEach { it.seen.complete(Unit) }
  }

  override fun onBind(intent: Intent?): IBinder? = null
}

@Provide @Element<AppScope> data class ForegroundServiceComponent(
  val foregroundManager: ForegroundManagerImpl,
  val notificationManager: @SystemService NotificationManager,
  val logger: Logger,
  val scope: NamedCoroutineScope<AppScope>
)
