package com.ivianuu.essentials.util

import android.os.*
import androidx.compose.runtime.*
import arrow.fx.coroutines.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*

@Stable @Provide class WakeLockManager(
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val powerManager: @SystemService PowerManager
) {
  @Composable fun WakeLock(id: SourceKey = inject) {
    LaunchedEffect(id) {
      withContext(coroutineContexts.io) {
        bracketCase(
          acquire = { powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, id.value) },
          use = {
            logger.d { "${id.value} acquire wake lock" }
            it.acquire()
          },
          release = { wakeLock, _ ->
            logger.d { "${id.value} release wake lock" }
            wakeLock.release()
          }
        )
      }
    }
  }
}
