/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.foreground

import android.app.*
import android.content.*
import androidx.compose.runtime.*
import androidx.core.content.*
import essentials.*
import injekt.*

@Stable @Provide @Scoped<AppScope> class ForegroundManager(
  private val context: Application,
  @property:Provide private val logger: Logger
) {
  internal var states by mutableStateOf(emptyList<ForegroundState>())
    private set

  @Composable fun Foreground(
    id: String,
    removeNotification: Boolean = true,
    notification: (@Composable () -> Notification)? = null,
  ) {
    key(id) {
      val state = remember {
        ForegroundState(id, removeNotification, notification)
      }
      state.removeNotification = removeNotification
      state.notification = notification

      DisposableEffect(state) {
        states += state
        d { "add state $id $states" }
        onDispose {
          states -= state
          d { "remove state $id $states" }
        }
      }

      LaunchedEffect(true) {
        d { "start foreground service $id $states" }
        ContextCompat.startForegroundService(
          context,
          Intent(context, ForegroundService::class.java)
        )
      }
    }
  }

  @Stable internal class ForegroundState(
    val id: String,
    removeNotification: Boolean,
    notification: (@Composable () -> Notification)?,
  ) {
    var removeNotification by mutableStateOf(removeNotification)
    var notification by mutableStateOf(notification)
  }
}

@Provide data object ForegroundScope : ChildScopeMarker<ForegroundScope, AppScope>
