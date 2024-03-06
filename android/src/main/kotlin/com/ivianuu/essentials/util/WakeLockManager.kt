package com.ivianuu.essentials.util

import android.os.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide class WakeLockManager(
  private val logger: Logger,
  private val powerManager: @SystemService PowerManager
) {
  @Composable fun WakeLock(id: SourceKey = inject) {
    DisposableEffect(id) {
      logger.d { "${id.value} acquire wake lock" }
      val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, id.value)
        .also { it.acquire() }
      onDispose {
        logger.d { "${id.value} release wake lock" }
        wakeLock.release()
      }
    }
  }
}
