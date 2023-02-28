/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.essentials.coroutines.par
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.common.SourceKey
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ForegroundManager {
  suspend fun <R> runInForeground(
    notification: Notification,
    @Inject foregroundId: ForegroundId,
    block: suspend ForegroundScope.() -> R
  ): R
}

interface ForegroundScope : CoroutineScope {
  suspend fun updateNotification(notification: Notification)
}

fun Flow<Notification>.updateNotification(scope: ForegroundScope) {
  onEach { scope.updateNotification(it) }
    .launchIn(scope)
}

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
    notification: Notification,
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
    notification: Notification,
    @Inject scope: CoroutineScope
  ) : ForegroundScope, CoroutineScope by scope {
    val notification = MutableStateFlow(notification)
    private val lock = Mutex()
    val seen = CompletableDeferred<Unit>()

    override suspend fun updateNotification(notification: Notification) = lock.withLock {
      this.notification.value = notification
    }
  }
}
