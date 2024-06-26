/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.*
import android.app.Service
import android.content.*
import android.os.*
import androidx.compose.runtime.*
import app.cash.molecule.*
import arrow.fx.coroutines.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.time.*
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
    logger.d { "foreground service started" }

    job = scope.launchMolecule {
      val states = foregroundManager.states
      var removeServiceNotification by remember { mutableStateOf(true) }

      if (states.isEmpty()) {
        LaunchedEffect(removeServiceNotification) {
          onCancel(
            fa = {
              logger.d { "stop foreground -> remove notification $removeServiceNotification" }
              stopForeground(
                if (removeServiceNotification) STOP_FOREGROUND_REMOVE
                else STOP_FOREGROUND_DETACH
              )
              logger.d { "dispatch delayed foreground stop" }
              delay(6.seconds)
              stopSelf()
            },
            onCancel = { logger.d { "cancel delayed foreground stop" } }
          )
        }
      } else {
        val notifications = states.mapNotNullTo(mutableListOf()) { state ->
          key(state.id) {
            state.notification?.invoke()
              ?.let { Triple(state.id, state.removeNotification, it) }
          }
        }

        if (notifications.isEmpty())
          notifications += remember {
            Triple(
              "default_foreground_id",
              true,
              notificationFactory.create(
                "default_foreground",
                "Foreground",
                NotificationManager.IMPORTANCE_LOW
              ) {
                setContentTitle("${appConfig.appName} is running!")
                setSmallIcon(R.drawable.ic_sync)
                setContentIntent(remoteActionFactory<StartAppRemoteAction, _>())
              }
            )
          }

        notifications.forEachIndexed { index, (id, removeNotification, notification) ->
          key(id) {
            DisposableEffect(index, id, notification) {
              logger.d { "$id update foreground notification" }

              if (index == 0) {
                removeServiceNotification = removeNotification
                startForeground(id.hashCode(), notification)
                onDispose {  }
              } else {
                notificationManager.notify(id.hashCode(), notification)
                onDispose {
                  if (removeNotification)
                    notificationManager.cancel(id.hashCode())
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
    logger.d { "stop foreground service" }
    job?.cancel()
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null
}
