/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.*
import android.content.*
import androidx.compose.runtime.*
import androidx.core.content.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*

@Provide @Scoped<AppScope> class ForegroundManager(
  private val appContext: AppContext,
  private val logger: Logger
) {
  internal val states = MutableStateFlow(emptyList<ForegroundState>())
  private val lock = Mutex()

  suspend fun startForeground(
    removeNotification: Boolean = true,
    id: String,
    notification: (@Composable () -> Notification)?,
  ): Nothing = bracketCase(
    acquire = {
      ForegroundState(id, removeNotification, notification)
        .also {
          lock.withLock { states.value = states.value + it }
          logger.d { "start foreground $id ${states.value}" }

          ContextCompat.startForegroundService(
            appContext,
            Intent(appContext, ForegroundService::class.java)
          )
        }
    },
    release = { state, _ ->
      state.seen.await()
      lock.withLock { states.value = states.value - state }
      logger.d { "stop foreground $id ${states.value}" }
    }
  )

  internal class ForegroundState(
    val id: String,
    val removeNotification: Boolean,
    val notification: (@Composable () -> Notification)?,
  ) {
    val seen = CompletableDeferred<Unit>()
  }
}

data object ForegroundScope
