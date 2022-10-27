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
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.common.SourceKey
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ForegroundManager {
  suspend fun <R> runInForeground(
    notification: Notification,
    @Inject id: ForegroundId,
    block: suspend ForegroundScope.() -> R
  ): R
}

interface ForegroundScope {
  suspend fun updateNotification(notification: Notification)
}

@JvmInline value class ForegroundId(val value: Int) {
  companion object {
    @Provide fun default(sourceKey: SourceKey) = ForegroundId(sourceKey.hashCode())
  }
}

@Provide @Scoped<AppScope> class ForegroundManagerImpl(
  private val context: AppContext,
  private val L: Logger
) : ForegroundManager {
  private val mainLock = Mutex()

  private val idLocks = mutableMapOf<Int, Mutex>()

  internal val states = MutableStateFlow(emptyList<ForegroundState>())

  override suspend fun <R> runInForeground(
    notification: Notification,
    @Inject id: ForegroundId,
    block: suspend ForegroundScope.() -> R
  ) = bracket(
    acquire = {
      val idLock = mainLock.withLock {
        idLocks.getOrPut(id.value) { Mutex() }
      }

      val state = ForegroundState(id.value, notification)

      idLock.withLock {
        mainLock.withLock { states.value = states.value + state }
        log { "start foreground ${id.value} ${states.value}" }

        ContextCompat.startForegroundService(
          context,
          Intent(context, ForegroundService::class.java)
        )

        // we ensure that the foreground service has seen this foreground request
        // to prevent a crash in the android system
        state.seen.await()

        state
      }
    },
    use = block,
    release = { state, _ ->
      val idLock = mainLock.withLock { idLocks[id.value]!! }
      idLock.withLock {
        mainLock.withLock { states.value = states.value - state }
        log { "stop foreground $id ${states.value}" }
      }
    }
  )

  internal class ForegroundState(val id: Int, notification: Notification) : ForegroundScope {
    val notification = MutableStateFlow(notification)
    private val mutex = Mutex()
    val seen = CompletableDeferred<Unit>()

    override suspend fun updateNotification(notification: Notification) = mutex.withLock {
      this.notification.value = notification
    }
  }
}
