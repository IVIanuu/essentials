/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.*
import android.app.Service
import android.content.*
import android.os.*
import androidx.compose.runtime.*
import androidx.compose.ui.util.*
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
import kotlin.math.*
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
      logger.d { "compose main body" }

      DisposableEffect(true) {
        val foregroundScope = foregroundScopeFactory()
        onDispose { foregroundScope.dispose() }
      }

      val defaultState = remember {
        ForegroundManager.ForegroundState(
          "default_foreground_id",
          true
        ) {
          notificationFactory.create(
            "default_foreground",
            "Foreground",
            NotificationManager.IMPORTANCE_LOW
          ) {
            setContentTitle("${appConfig.appName} is running!")
            setSmallIcon(R.drawable.ic_sync)
            setContentIntent(remoteActionFactory<StartAppRemoteAction, _>())
          }
        }
      }

      val currentStates by remember {
        derivedStateOf {
          if (foregroundManager.states.isEmpty()) emptyList()
          else {
            val statesWithNotification =
              foregroundManager.states.count { it.notification != null }
            if (statesWithNotification > 0) foregroundManager.states
            else foregroundManager.states + defaultState
          }
        }
      }

      val mainState by remember {
        derivedStateOf { currentStates.firstOrNull() }
      }

      var removeServiceNotification by remember { mutableStateOf(true) }
      LaunchedEffect(mainState, mainState?.removeNotification) {
        if (mainState != null)
          removeServiceNotification = mainState!!.removeNotification
      }

      if (currentStates.isEmpty()) {
        LaunchedEffect(removeServiceNotification) {
          logger.d { "stop foreground -> remove notification $removeServiceNotification" }
          stopForeground(
            if (removeServiceNotification) STOP_FOREGROUND_REMOVE
            else STOP_FOREGROUND_DETACH
          )

          onCancel(
            fa = {
              logger.d { "dispatch delayed service stop" }
              delay(6.seconds)

              logger.d { "stop self" }
              stopSelf()
            }
          ) { logger.d { "cancel stopping" } }
        }
      } else {
        currentStates.fastForEach { state ->
          key(state.id) {
            ForegroundNotification(
              isMainNotification = { mainState == state },
              state = state
            )
          }
        }
      }
    }
  }

  @Composable private fun ForegroundNotification(
    isMainNotification: () -> Boolean,
    state: ForegroundManager.ForegroundState
  ) {
    logger.d { "compose notification ${state.id}" }
    val notification = state.notification?.invoke()
    DisposableEffect(notification, isMainNotification()) {
      logger.d { "${state.id} update foreground notification" }

      val notificationId = state.id.hashCode()
      if (isMainNotification()) {
        startForeground(notificationId, notification)
        onDispose {
        }
      } else {
        notificationManager.notify(notificationId, notification)
        onDispose {
          if (state.removeNotification)
            notificationManager.cancel(notificationId)
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
