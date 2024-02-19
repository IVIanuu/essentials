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
    @Inject foregroundId: ForegroundId,
    notification: (@Composable () -> Notification)?,
  ): Nothing = bracketCase(
    acquire = {
      ForegroundState(foregroundId.value, removeNotification, notification)
        .also {
          lock.withLock { states.value = states.value + it }
          logger.log { "start foreground ${foregroundId.value} ${states.value}" }

          ContextCompat.startForegroundService(
            appContext,
            Intent(appContext, ForegroundService::class.java)
          )
        }
    },
    release = { state, _ ->
      state.seen.await()
      lock.withLock { states.value = states.value - state }
      logger.log { "stop foreground $foregroundId ${states.value}" }
    }
  )

  internal class ForegroundState(
    val id: Int,
    val removeNotification: Boolean,
    val notification: (@Composable () -> Notification)?,
  ) {
    val seen = CompletableDeferred<Unit>()
  }
}

@JvmInline value class ForegroundId(val value: Int) {
  @Provide companion object {
    @Provide fun default(sourceKey: SourceKey) = ForegroundId(sourceKey.hashCode())
  }
}

data object ForegroundScope
