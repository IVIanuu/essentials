/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.foreground

import android.app.*
import android.app.Service
import android.content.*
import android.os.*
import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.app.*
import essentials.compose.*
import essentials.coroutines.*
import essentials.logging.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

@Stable @Provide @AndroidComponent class ForegroundService(
  private val appConfig: AppConfig,
  private val foregroundManager: ForegroundManager,
  private val notificationFactory: NotificationFactory,
  private val notificationManager: @SystemService NotificationManager,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val foregroundScopeFactory: () -> Scope<ForegroundScope>
) : Service() {
  private var job: Job? = null
  private var needsStartForegroundCall by mutableStateOf(true)

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
            setContentIntent(uiLauncherIntent())
          }
        }
      }

      val currentStates by remember {
        derivedStateOf {
          if (foregroundManager.states.isEmpty() && !needsStartForegroundCall) emptyList()
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

      if (currentStates.isEmpty() && !needsStartForegroundCall) {
        LaunchedEffect(removeServiceNotification) {
          delay(1.seconds)

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
              state = state,
              onStartForegroundCalled = { needsStartForegroundCall = false },
              shouldStartForeground = { mainState == state && needsStartForegroundCall }
            )
          }
        }
      }
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    logger.d { "on start command $intent" }
    needsStartForegroundCall = true
    return super.onStartCommand(intent, flags, startId)
  }

  @Composable private fun ForegroundNotification(
    state: ForegroundManager.ForegroundState,
    shouldStartForeground: () -> Boolean,
    onStartForegroundCalled: () -> Unit
  ) {
    val notification = state.notification?.invoke()
    val notificationId = state.id.hashCode()
    if (notification != null) {
      DisposableEffect(notificationId) {
        onDispose {
          if (state.removeNotification) {
            logger.d { "remove notification $notificationId" }
            notificationManager.cancel(notificationId)
          }
        }
      }

      if (shouldStartForeground())
        DisposableEffect(true) {
          logger.d { "${state.id} call start foreground" }
          startForeground(notificationId, notification)
          onStartForegroundCalled()
          onDispose {
          }
        }

      DisposableEffect(notification) {
        logger.d { "${state.id} update notification" }
        notificationManager.notify(notificationId, notification)
        onDispose {
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
