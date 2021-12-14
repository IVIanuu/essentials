/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.*
import android.content.*
import android.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ForegroundService : Service() {
  private val component by lazy {
    application
      .cast<AppElementsOwner>()
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
              combine(
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
