/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.Notification
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.coroutines.bracket
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
  @Composable fun Foreground(
    @Inject foregroundId: ForegroundId,
    notification: @Composable () -> Notification
  )
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

  @Composable override fun Foreground(
    @Inject foregroundId: ForegroundId,
    notification: @Composable () -> Notification
  ) {
    val state = remember(this, foregroundId) {
      ForegroundState(foregroundId.value, notification)
    }
    state.notification = notification

    DisposableEffect(state) {
      logger.log { "start foreground ${foregroundId.value} ${states.value}" }

      ContextCompat.startForegroundService(
        appContext,
        Intent(appContext, ForegroundService::class.java)
      )

      onDispose {
        logger.log { "stop foreground $foregroundId ${states.value}" }
      }
    }

    LaunchedEffect(state) {
      bracket(
        acquire = { lock.withLock { states.value = states.value + state } },
        use = {
          state.seen.await()
          awaitCancellation()
        },
        release = { _, _ -> lock.withLock { states.value = states.value - state } }
      )
    }
  }

  internal class ForegroundState(val id: Int, notification: @Composable () -> Notification) {
    var notification by mutableStateOf(notification)
    val seen = CompletableDeferred<Unit>()
  }
}
