/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.foreground

import android.app.*
import android.content.*
import androidx.compose.runtime.*
import androidx.core.content.*
import essentials.*
import essentials.logging.*
import injekt.*

@Composable fun Foreground(
  id: String,
  removeNotification: Boolean = true,
  scope: Scope<*> = inject,
  notification: (@Composable () -> Notification)? = null,
) {
  val serviceState = service<ForegroundServiceState>()
  key(id) {
    val state = remember {
      ForegroundState(id, removeNotification, notification)
    }
    state.removeNotification = removeNotification
    state.notification = notification

    DisposableEffect(state) {
      serviceState.states += state
      d { "add state $id ${serviceState.states}" }
      onDispose {
        serviceState.states -= state
        d { "remove state $id ${serviceState.states}" }
      }
    }

    LaunchedEffect(true) {
      d { "start foreground service $id ${serviceState.states}" }
      ContextCompat.startForegroundService(
        appContext(),
        Intent(appContext(), ForegroundService::class.java)
      )
    }
  }
}

@Provide @ScopedService<AppScope> class ForegroundServiceState {
  internal var states by mutableStateOf(emptyList<ForegroundState>())
}

@Stable internal class ForegroundState(
  val id: String,
  removeNotification: Boolean,
  notification: (@Composable () -> Notification)?,
) {
  var removeNotification by mutableStateOf(removeNotification)
  var notification by mutableStateOf(notification)
}

data object ForegroundScope
