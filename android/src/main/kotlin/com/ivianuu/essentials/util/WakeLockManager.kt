package com.ivianuu.essentials.util

import android.os.PowerManager
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.coroutines.bracketCase
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.SourceKey

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
