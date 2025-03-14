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
  private val scope: ScopedCoroutineScope<AppScope> = inject,
  private val foregroundScopeFactory: () -> Scope<ForegroundScope>
) : Service() {
  private var job: Job? = null
  private var needsStartForegroundCall by mutableStateOf(true)

  override fun onCreate() {
    super.onCreate()
    d { "foreground service started" }

    val foregroundServiceState = service<ForegroundServiceState>()
    job = scope.launchMolecule {
      d { "compose main body" }

      DisposableEffect(true) {
        val foregroundScope = foregroundScopeFactory()
        onDispose { foregroundScope.dispose() }
      }

      val defaultState = remember {
        ForegroundState(
          "default_foreground_id",
          true
        ) {
          /*buildNotification(
            "default_foreground",
            "Foreground",
            NotificationManager.IMPORTANCE_LOW
          ) {
            setContentTitle("${appConfig().appName} is running!")
            setSmallIcon(R.drawable.ic_sync)
            setContentIntent(uiLauncherIntent())
          }*/ TODO()
        }
      }

      val currentStates by remember {
        derivedStateOf {
          if (foregroundServiceState.states.isEmpty() && !needsStartForegroundCall) emptyList()
          else {
            val statesWithNotification =
              foregroundServiceState.states.count { it.notification != null }
            if (statesWithNotification > 0) foregroundServiceState.states
            else foregroundServiceState.states + defaultState
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

          d { "stop foreground -> remove notification $removeServiceNotification" }
          stopForeground(
            if (removeServiceNotification) STOP_FOREGROUND_REMOVE
            else STOP_FOREGROUND_DETACH
          )

          onCancel(
            fa = {
              d { "dispatch delayed service stop" }
              delay(6.seconds)

              d { "stop self" }
              stopSelf()
            }
          ) { d { "cancel stopping" } }
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
    d { "on start command $intent" }
    needsStartForegroundCall = true
    return super.onStartCommand(intent, flags, startId)
  }

  @Composable private fun ForegroundNotification(
    state: ForegroundState,
    shouldStartForeground: () -> Boolean,
    onStartForegroundCalled: () -> Unit
  ) {
    val notification = state.notification?.invoke()
    if (notification != null) {
      val notificationId = state.id.hashCode()
      DisposableEffect(notificationId) {
        onDispose {
          if (state.removeNotification) {
            d { "remove notification $notificationId" }
            systemService<NotificationManager>().cancel(notificationId)
          }
        }
      }

      if (shouldStartForeground())
        DisposableEffect(true) {
          d { "${state.id} call start foreground" }
          startForeground(notificationId, notification)
          onStartForegroundCalled()
          onDispose {
          }
        }

      DisposableEffect(notification) {
        d { "${state.id} update notification" }
        systemService<NotificationManager>().notify(notificationId, notification)
        onDispose {
        }
      }
    }
  }

  override fun onDestroy() {
    d { "stop foreground service" }
    job?.cancel()
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null
}
