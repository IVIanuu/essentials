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
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ForegroundManager {
  suspend fun startForeground(id: Int, notification: StateFlow<Notification>): Nothing
}

suspend fun ForegroundManager.startForeground(id: Int, notification: Notification): Nothing =
  startForeground(id, MutableStateFlow(notification))

@Provide @Scoped<AppScope> class ForegroundManagerImpl(
  private val context: AppContext,
  private val L: Logger
) : ForegroundManager {
  private val lock = Mutex()

  internal val states = MutableStateFlow(emptyList<ForegroundState>())

  override suspend fun startForeground(
    id: Int,
    notification: StateFlow<Notification>
  ): Nothing = bracket(
    acquire = {
      lock.withLock {
        ForegroundState(id, notification)
          .also {
            states.value = states.value + it
            log { "start foreground $id ${states.value}" }
          }
      }
    },
    use = {
      ContextCompat.startForegroundService(
        context,
        Intent(context, ForegroundService::class.java)
      )

      awaitCancellation()
    },
    release = { state, _ ->
      lock.withLock {
        // we ensure that the foreground service has seen this foreground request
        // to prevent a crash in the android system
        state.seen.await()

        states.value = states.value - state
        log { "stop foreground ${state.id} ${states.value}" }
      }
    }
  )

  internal class ForegroundState(val id: Int, val notification: StateFlow<Notification>) {
    val seen = CompletableDeferred<Unit>()
  }
}
