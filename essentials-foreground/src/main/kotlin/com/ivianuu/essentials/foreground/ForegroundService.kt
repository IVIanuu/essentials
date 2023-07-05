/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.compose.launchComposition
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.time.seconds
import com.ivianuu.essentials.util.NotificationManager
import com.ivianuu.essentials.util.id
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

@Provide @AndroidComponent class ForegroundService(
  private val foregroundManager: ForegroundManagerImpl,
  private val notificationManager: NotificationManager,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<AppScope>
) : Service() {
  private var job: Job? = null

  override fun onCreate() {
    super.onCreate()
    logger.log { "start service" }

    job = scope.launchComposition {
      val states = foregroundManager.states.collectAsState().value
      states.forEachIndexed { index, state ->
        key(index, state) {
          if (index == 0) {
            LaunchedEffect(true) {
              notificationManager.notificationModels(this, state.notification)
                .map { notificationManager.toAndroidNotification(state.notification, it) }
                .collectLatest {
                  startForeground(it.id.value, it)
                  state.seen.complete(Unit)
                }
            }
          } else {
            LaunchedEffect(true) {
              state.seen.complete(Unit)
              notificationManager.postNotification(state.notification)
            }
          }
        }
      }

      if (states.isEmpty())
        LaunchedEffect(true) {
          onCancel(
            block = {
              logger.log { "stop foreground" }
              stopForeground(true)
              logger.log { "dispatch delayed stop" }
              delay(6.seconds)
              stopSelf()
            },
            onCancel = { logger.log { "cancel delayed stop" } }
          )
        }
    }
  }

  override fun onDestroy() {
    logger.log { "stop service" }
    job?.cancel()
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null
}
