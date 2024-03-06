/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.*
import android.content.*
import androidx.compose.runtime.*
import androidx.core.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*

@Provide @Scoped<AppScope> class ForegroundManager(
  private val appContext: AppContext,
  private val logger: Logger
) {
  internal var states = emptyList<ForegroundState>()
    private set

  @Composable fun Foreground(
    id: SourceKey = inject,
    removeNotification: Boolean = true,
    notification: (@Composable () -> Notification)? = null,
  ) {
    val state = remember(id.value) { ForegroundState(id.value, removeNotification, notification) }

    LaunchedEffect(state) {
      states += state
      logger.d { "start foreground ${id.value} $states" }
      ContextCompat.startForegroundService(
        appContext,
        Intent(appContext, ForegroundService::class.java)
      )
      onCancel {
        state.seen.await()
        states -= state
        logger.d { "stop foreground ${id.value} $states" }
      }
    }
  }

  internal class ForegroundState(
    val id: String,
    removeNotification: Boolean,
    notification: (@Composable () -> Notification)?,
  ) {
    val seen = CompletableDeferred<Unit>()
    var removeNotification by mutableStateOf(removeNotification)
    var notification by mutableStateOf(notification)
  }
}

data object ForegroundScope
