package com.ivianuu.essentials.util

import android.annotation.SuppressLint
import android.os.PowerManager
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.SourceKey

fun interface WakeLock {
  suspend fun acquire(@Inject id: WakeLockId): Nothing
}

@JvmInline value class WakeLockId(val value: String) {
  companion object {
    @Provide fun default(sourceKey: SourceKey) = WakeLockId(sourceKey.value)
  }
}

@SuppressLint("WakelockTimeout")
@Provide fun wakeLock(powerManager: @SystemService PowerManager) = WakeLock { id ->
  bracket(
    acquire = {
      powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, id.value)
        .also { it.acquire() }
    },
    release = { wakeLock, _ -> wakeLock.release() }
  )
}
