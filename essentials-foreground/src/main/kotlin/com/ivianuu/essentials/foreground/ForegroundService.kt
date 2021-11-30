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
import com.ivianuu.essentials.state.*
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

  private var previousStates = emptyList<Pair<ForegroundManagerImpl.ForegroundState, Notification>>()

  private var job: Job? = null

  override fun onCreate() {
    super.onCreate()
    log { "start foreground service" }

    job = component.scope.launch(start = CoroutineStart.UNDISPATCHED) {
      guarantee(
        block = {
          composedFlow {
            component.foregroundManager.states
              .map { it to it.notification() }
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

  private fun applyState(states: List<Pair<ForegroundManagerImpl.ForegroundState, Notification>>) {
    log { "apply states: $states" }

    previousStates
      .filter { state -> states.none { state.first.id == it.first.id } }
      .forEach { component.notificationManager.cancel(it.first.id) }

    if (states.isNotEmpty()) {
      states
        .forEachIndexed { index, (state, notification) ->
          if (index == 0) {
            startForeground(state.id, notification)
          } else {
            component.notificationManager.notify(state.id, notification)
          }
        }
    } else {
      stopForeground(true)
      stopSelf()
    }

    states.forEach { it.first.seen.complete(Unit) }

    previousStates = states
  }

  override fun onBind(intent: Intent?): IBinder? = null
}

@Provide @Element<AppScope> data class ForegroundServiceComponent(
  val foregroundManager: ForegroundManagerImpl,
  val notificationManager: @SystemService NotificationManager,
  val logger: Logger,
  val scope: NamedCoroutineScope<AppScope>
)
