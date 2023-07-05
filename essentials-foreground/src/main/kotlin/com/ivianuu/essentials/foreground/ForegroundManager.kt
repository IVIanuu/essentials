/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.content.Intent
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.util.Notification
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ForegroundManager {
  suspend fun startForeground(notification: Notification): Nothing
}

@Provide @Scoped<AppScope> class ForegroundManagerImpl(
  private val appContext: AppContext,
  private val logger: Logger
) : ForegroundManager {
  internal val states = MutableStateFlow(emptyList<ForegroundState>())
  private val lock = Mutex()

  override suspend fun startForeground(notification: Notification) = bracket(
    acquire = {
      ForegroundState(notification)
        .also {
          lock.withLock { states.value = states.value + it }
          logger.log { "start foreground $notification ${states.value}" }

          ContextCompat.startForegroundService(
            appContext,
            Intent(appContext, ForegroundService::class.java)
          )
        }
    },
    release = { state, _ ->
      state.seen.await()
      lock.withLock { states.value = states.value - state }
      logger.log { "stop foreground $notification ${states.value}" }
    }
  )

  internal class ForegroundState(val notification: Notification) {
    val seen = CompletableDeferred<Unit>()
  }
}
