package com.ivianuu.essentials.util

import android.os.PowerManager
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.SourceKey

interface WakeLockManager {
  context(WakeLockId) suspend fun acquire(): Nothing
}

@JvmInline value class WakeLockId(val wakeLockId: String) {
  @Provide companion object {
    @Provide fun default(sourceKey: SourceKey) = WakeLockId(sourceKey.value)
  }
}

context(Logger) @Provide class WakeLockManagerImpl(
  private val powerManager: @SystemService PowerManager
) : WakeLockManager {
  context(WakeLockId) override suspend fun acquire() = bracket(
    acquire = {
      log { "$wakeLockId acquire" }
      powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, wakeLockId)
        .also { it.acquire() }
    },
    release = { wakeLock, _ ->
      log { "$wakeLockId release" }
      wakeLock.release()
    }
  )
}
