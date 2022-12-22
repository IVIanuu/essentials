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
  context(ForegroundId) suspend fun <R> runInForeground(
    notification: Notification,
    block: suspend context(ForegroundScope) () -> R
  ): R
}

interface ForegroundScope : CoroutineScope {
  suspend fun updateNotification(notification: Notification)
}

context(ForegroundScope)
fun Flow<Notification>.updateNotification() {
  onEach { updateNotification(it) }
    .launchIn(this@ForegroundScope)
}

@JvmInline value class ForegroundId(val foregroundId: Int) {
  companion object {
    @Provide fun default(sourceKey: SourceKey) = ForegroundId(sourceKey.hashCode())
  }
}

context(Logger) @Provide @Scoped<AppScope> class ForegroundManagerImpl(
  private val context: AppContext
) : ForegroundManager {
  internal val states = MutableStateFlow(emptyList<ForegroundState>())
  private val lock = Mutex()

  context(ForegroundId) override suspend fun <R> runInForeground(
    notification: Notification,
    block: suspend context(ForegroundScope) () -> R
  ) = coroutineScope {
    bracket(
      acquire = {
        val state = ForegroundState(foregroundId, notification, this)
        lock.withLock { states.value = states.value + state }
        log { "start foreground $foregroundId ${states.value}" }

        ContextCompat.startForegroundService(
          context,
          Intent(context, ForegroundService::class.java)
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
        log { "stop foreground $foregroundId ${states.value}" }
      }
    )
  }

  internal class ForegroundState(
    val id: Int,
    notification: Notification,
    coroutineScope: CoroutineScope
  ) : ForegroundScope, CoroutineScope by coroutineScope {
    val notification = MutableStateFlow(notification)
    private val lock = Mutex()
    val seen = CompletableDeferred<Unit>()

    override suspend fun updateNotification(notification: Notification) = lock.withLock {
      this.notification.value = notification
    }
  }
}
