/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.*
import android.app.Service
import android.content.*
import android.os.*
import androidx.compose.runtime.*
import arrow.fx.coroutines.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.coroutines.RateLimiter
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

@Provide @AndroidComponent class ForegroundService(
  private val appConfig: AppConfig,
  private val foregroundManager: ForegroundManager,
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
                  setSmallIcon(R.drawable.ic_sync)
                  setContentIntent(remoteActionFactory<StartAppRemoteAction, _>())
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
