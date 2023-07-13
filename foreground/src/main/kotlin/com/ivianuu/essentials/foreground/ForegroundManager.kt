/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.Notification
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.SourceKey
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ForegroundManager {
  context(ForegroundId) suspend fun startForeground(
    notification: @Composable () -> Notification,
  ): Nothing
}

@JvmInline value class ForegroundId(val foregroundId: Int) {
  @Provide companion object {
    @Provide fun default(sourceKey: SourceKey) = ForegroundId(sourceKey.hashCode())
  }
}

context(Logger) @Provide @Scoped<AppScope> class ForegroundManagerImpl(
  private val appContext: AppContext
) : ForegroundManager {
  internal val states = MutableStateFlow(emptyList<ForegroundState>())
  private val lock = Mutex()

  context(ForegroundId)
  override suspend fun startForeground(notification: @Composable () -> Notification) = bracket(
    acquire = {
      ForegroundState(foregroundId, notification)
        .also {
          lock.withLock { states.value = states.value + it }
          log { "start foreground $foregroundId ${states.value}" }

          ContextCompat.startForegroundService(
            appContext,
            Intent(appContext, ForegroundService::class.java)
          )
        }
    },
    release = { state, _ ->
      state.seen.await()
      lock.withLock { states.value = states.value - state }
      log { "stop foreground $foregroundId ${states.value}" }
    }
  )

  internal class ForegroundState(val id: Int, val notification: @Composable () -> Notification) {
    val seen = CompletableDeferred<Unit>()
  }
}
