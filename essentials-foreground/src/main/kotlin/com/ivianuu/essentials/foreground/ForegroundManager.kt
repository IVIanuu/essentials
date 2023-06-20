/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.Notification
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.essentials.coroutines.par
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.SourceKey
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ForegroundManager {
  suspend fun <R> runInForeground(
    notification: @Composable () -> Notification,
    @Inject foregroundId: ForegroundId,
    block: suspend ForegroundScope.() -> R
  ): R
}

@Composable fun ForegroundManager.Foreground(
  @Inject foregroundId: ForegroundId,
  notification: @Composable () -> Notification
) {
  LaunchedEffect(this, foregroundId) {
    runInForeground(notification) { awaitCancellation() }
  }
}

interface ForegroundScope : CoroutineScope

@JvmInline value class ForegroundId(val value: Int) {
  companion object {
    @Provide fun default(sourceKey: SourceKey) = ForegroundId(sourceKey.hashCode())
  }
}

@Provide @Scoped<AppScope> class ForegroundManagerImpl(
  private val appContext: AppContext,
  private val logger: Logger
) : ForegroundManager {
  internal val states = MutableStateFlow(emptyList<ForegroundState>())
  private val lock = Mutex()

  override suspend fun <R> runInForeground(
    notification: @Composable () -> Notification,
    @Inject foregroundId: ForegroundId,
    block: suspend ForegroundScope.() -> R
  ) = coroutineScope {
    bracket(
      acquire = {
        val state = ForegroundState(foregroundId.value, notification)
        lock.withLock { states.value = states.value + state }
        logger.log { "start foreground ${foregroundId.value} ${states.value}" }

        ContextCompat.startForegroundService(
          appContext,
          Intent(appContext, ForegroundService::class.java)
        )

        state
      },
      use = {
        par(
          { block(it) },
          { it.seen.await() }
        ).first() as R
      },
      release = { state, _ ->
        lock.withLock { states.value = states.value - state }
        logger.log { "stop foreground $foregroundId ${states.value}" }
      }
    )
  }

  internal class ForegroundState(
    val id: Int,
    val notification: @Composable () -> Notification,
    @Inject scope: CoroutineScope
  ) : ForegroundScope, CoroutineScope by scope {
    val seen = CompletableDeferred<Unit>()
  }
}
