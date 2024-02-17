package com.ivianuu.essentials.util

import android.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide class WakeLockManager(
  private val logger: Logger,
  private val powerManager: @SystemService PowerManager
) {
  suspend fun acquire(@Inject id: WakeLockId): Nothing = bracketCase(
    acquire = {
      logger.log { "${id.value} acquire" }
      powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, id.value)
        .also { it.acquire() }
    },
    release = { wakeLock, _ ->
      logger.log { "${id.value} release" }
      wakeLock.release()
    }
  )
}

@JvmInline value class WakeLockId(val value: String) {
  @Provide companion object {
    @Provide fun default(sourceKey: SourceKey) = WakeLockId(sourceKey.value)
  }
}
