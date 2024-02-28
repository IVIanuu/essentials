package com.ivianuu.essentials.util

import android.os.*
import androidx.compose.runtime.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*

@Provide class WakeLockManager(
  private val logger: Logger,
  private val powerManager: @SystemService PowerManager
) {
  @Composable fun WakeLock(id: String) {
    DisposableEffect(id) {
      logger.d { "$id acquire wake lock" }
      val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, id)
        .also { it.acquire() }
      onDispose {
        logger.d { "$id release wake lock" }
        wakeLock.release()
      }
    }
  }
}
