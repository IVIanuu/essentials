/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.compose.launchComposition
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

context(Logger) @Provide @AndroidComponent class ForegroundService(
  private val foregroundManager: ForegroundManagerImpl,
  private val notificationManager: @SystemService NotificationManager,
  private val scope: ScopedCoroutineScope<AppScope>
) : Service() {
  private var job: Job? = null

  override fun onCreate() {
    super.onCreate()
    log { "start service" }

    job = scope.launchComposition {
      val states by foregroundManager.states.collectAsState()

      states.forEachIndexed { index, state ->
        val notification = key(state.id) { state.notification() }

        key(index) {
          DisposableEffect(state, notification) {
            log { "update ${state.id}" }

            if (index == 0) {
              startForeground(state.id, notification)
            } else {
              notificationManager.notify(state.id, notification)
            }

            state.seen.complete(Unit)

            onDispose { notificationManager.cancel(state.id) }
          }
        }
      }

      if (states.isEmpty())
        LaunchedEffect(true) {
          onCancel(
            block = {
              log { "stop foreground" }
              stopForeground(true)
              log { "dispatch delayed stop" }
              delay(6.seconds)
              stopSelf()
            },
            onCancel = { log { "cancel delayed stop" } }
          )
        }
    }
  }

  override fun onDestroy() {
    log { "stop service" }
    job?.cancel()
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null
}
