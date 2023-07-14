package com.ivianuu.essentials.util

import android.os.PowerManager
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.SourceKey

interface WakeLockManager {
  suspend fun acquire(@Inject id: WakeLockId): Nothing
}

@JvmInline value class WakeLockId(val value: String) {
  @Provide companion object {
    @Provide fun default(sourceKey: SourceKey) = WakeLockId(sourceKey.value)
  }
}

@Provide class WakeLockManagerImpl(
  private val logger: Logger,
  private val powerManager: @SystemService PowerManager
) : WakeLockManager {
  override suspend fun acquire(@Inject id: WakeLockId) =
    bracket(
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
