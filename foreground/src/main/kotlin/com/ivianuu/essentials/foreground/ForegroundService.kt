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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import arrow.fx.coroutines.onCancel
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.compose.asFrameClock
import com.ivianuu.essentials.compose.launchComposition
import com.ivianuu.essentials.coroutines.RateLimiter
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.essentials.util.RemoteActionFactory
import com.ivianuu.essentials.util.StartAppRemoteAction
import com.ivianuu.essentials.util.context
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Provide @AndroidComponent class ForegroundService(
  private val appConfig: AppConfig,
  private val foregroundManager: ForegroundManagerImpl,
  private val notificationFactory: NotificationFactory,
  private val notificationManager: @SystemService NotificationManager,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val foregroundScopeFactory: () -> Scope<ForegroundScope>,
  private val remoteActionFactory: RemoteActionFactory
) : Service() {
  private var job: Job? = null

  override fun onCreate() {
    super.onCreate()
    logger.log { "start service" }

    job = scope.launchComposition(
      context = RateLimiter(1, 1.seconds)
        .asFrameClock()
    ) {
      val states by foregroundManager.states.collectAsState()
      var removeServiceNotification by remember { mutableStateOf(true) }

      if (states.isEmpty()) {
        LaunchedEffect(true, removeServiceNotification) {
          onCancel(
            fa = {
              logger.log { "stop foreground -> remove notification $removeServiceNotification" }
              stopForeground(
                if (removeServiceNotification) STOP_FOREGROUND_REMOVE
                else STOP_FOREGROUND_DETACH
              )
              logger.log { "dispatch delayed stop" }
              delay(6.seconds)
              stopSelf()
            },
            onCancel = { logger.log { "cancel delayed stop" } }
          )
        }
      } else {
        ((if (states.any { it.notification == null })
          remember(states.any { it.notification == null }) {
            listOf(
              Triple(
                inject<ForegroundId>().value,
                true,
                notificationFactory(
                  "default_foreground",
                  "Foreground",
                  NotificationManager.IMPORTANCE_LOW
                ) {
                  setContentTitle("${appConfig.appName} is running!")
                  setSmallIcon(R.drawable.es_ic_sync)
                  setContentIntent(remoteActionFactory<StartAppRemoteAction, _>(context))
                }
              )
            )
          }
        else emptyList()) + states
          .mapNotNull { state ->
            key(state.id) {
              state.notification?.invoke()
                ?.let { Triple(state.id, state.removeNotification, it) }
            }
          })
          .forEachIndexed { index, (id, removeNotification, notification) ->
            key(id) {
              DisposableEffect(index, id, notification) {
                logger.log { "update $id" }

                if (index == 0) {
                  removeServiceNotification = removeNotification
                  startForeground(id, notification)
                  onDispose {  }
                } else {
                  notificationManager.notify(id, notification)
                  onDispose {
                    if (removeNotification)
                      notificationManager.cancel(id)
                  }
                }
              }
            }
          }

        LaunchedEffect(states) {
          states.forEach { it.seen.complete(Unit) }
        }

        DisposableEffect(true) {
          val foregroundScope = foregroundScopeFactory()
          onDispose { foregroundScope.dispose() }
        }
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
