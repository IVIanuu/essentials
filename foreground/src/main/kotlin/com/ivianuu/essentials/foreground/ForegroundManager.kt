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
  internal var states = emptyList<ForegroundState>()
    private set
  private val lock = Mutex()

  suspend fun startForeground(
    id: String,
    removeNotification: Boolean = true,
    notification: (@Composable () -> Notification)? = null,
  ): Nothing = bracketCase(
    acquire = {
      ForegroundState(id, removeNotification, notification)
        .also {
          lock.withLock { states += it }
          logger.d { "start foreground $id $states" }

          ContextCompat.startForegroundService(
            appContext,
            Intent(appContext, ForegroundService::class.java)
          )
        }
    },
    release = { state, _ ->
      state.seen.await()
      lock.withLock { states -= state }
      logger.d { "stop foreground $id $states" }
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
