/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.*
import android.content.*
import androidx.compose.runtime.*
import androidx.core.content.*
import arrow.fx.coroutines.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import injekt.*
import injekt.common.*
import kotlinx.coroutines.*

@Stable @Provide @Scoped<AppScope> class ForegroundManager(
  private val appContext: AppContext,
  private val logger: Logger
) {
  internal var states by mutableStateOf(emptyList<ForegroundState>())
    private set

  @Composable fun Foreground(
    id: SourceKey = inject,
    removeNotification: Boolean = true,
    notification: (@Composable () -> Notification)? = null,
  ) {
    key(id.value) {
      val state = remember {
        ForegroundState(id.value, removeNotification, notification)
      }
      state.removeNotification = removeNotification
      state.notification = notification

      DisposableEffect(state) {
        states += state
        logger.d { "start foreground ${id.value} $states" }
        onDispose {
          states -= state
          logger.d { "stop foreground ${id.value} $states" }
        }
      }

      LaunchedEffect(true) {
        ContextCompat.startForegroundService(
          appContext,
          Intent(appContext, ForegroundService::class.java)
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

data object ForegroundScope
