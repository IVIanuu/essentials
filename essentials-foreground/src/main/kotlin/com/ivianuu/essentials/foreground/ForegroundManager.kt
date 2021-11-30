/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.*
import android.content.*
import androidx.compose.runtime.*
import androidx.core.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

interface ForegroundManager {
  suspend fun startForeground(id: Int, notification: @Composable () -> Notification): Nothing
}

suspend fun ForegroundManager.startForeground(id: Int, notification: Notification): Nothing =
  startForeground(id) { notification }

@Provide @Scoped<AppScope> class ForegroundManagerImpl(
  private val context: AppContext,
  private val L: Logger
) : ForegroundManager {
  private val lock = Mutex()

  internal var states = mutableStateListOf<ForegroundState>()
    private set

  override suspend fun startForeground(
    id: Int,
    notification: @Composable () -> Notification
  ): Nothing = bracket(
    acquire = {
      lock.withLock {
        ForegroundState(id, notification)
          .also {
            states += it
            log { "start foreground $id $states" }
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
      // we ensure that the foreground se#rvice has seen this foreground request
      // to prevent a crash in the android system
      state.seen.await()

      lock.withLock {
        states -= state
        log { "stop foreground ${state.id} $states" }
      }
    }
  )

  internal class ForegroundState(val id: Int, val notification: @Composable () -> Notification) {
    val seen = CompletableDeferred<Unit>()
  }
}
