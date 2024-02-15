/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.Notification
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import arrow.fx.coroutines.bracketCase
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.coroutines.bracketCase
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.SourceKey
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ForegroundManager {
  suspend fun startForeground(
    removeNotification: Boolean = true,
    @Inject foregroundId: ForegroundId,
    notification: (@Composable () -> Notification)? = null,
  ): Nothing
}

@JvmInline value class ForegroundId(val value: Int) {
  @Provide companion object {
    @Provide fun default(sourceKey: SourceKey) = ForegroundId(sourceKey.hashCode())
  }
}

data object ForegroundScope

@Provide @Scoped<AppScope> class ForegroundManagerImpl(
  private val appContext: AppContext,
  private val logger: Logger
) : ForegroundManager {
  internal val states = MutableStateFlow(emptyList<ForegroundState>())
  private val lock = Mutex()

  override suspend fun startForeground(
    removeNotification: Boolean,
    @Inject foregroundId: ForegroundId,
    notification: (@Composable () -> Notification)?,
  ) = bracketCase(
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
