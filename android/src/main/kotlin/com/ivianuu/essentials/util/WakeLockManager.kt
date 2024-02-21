package com.ivianuu.essentials.util

import android.os.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*

@Provide class WakeLockManager(
  private val logger: Logger,
  private val powerManager: @SystemService PowerManager
) {
  suspend fun acquire(id: String): Nothing = bracketCase(
    acquire = {
      logger.d { "$id acquire wake lock" }
      powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, id)
        .also { it.acquire() }
    },
    release = { wakeLock, _ ->
      logger.d { "$id release wake lock" }
      wakeLock.release()
    }
  )
}
