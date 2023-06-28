package com.ivianuu.essentials.util

import android.annotation.SuppressLint
import android.os.PowerManager
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.injekt.Provide

fun interface WakeLocker {
  suspend fun acquire(): Nothing
}

@SuppressLint("WakelockTimeout")
@Provide fun wakeLocker(
  powerManager: PowerManager
) = WakeLocker {
  bracket(
    acquire = {
      powerManager.newWakeLock(
        PowerManager.PARTIAL_WAKE_LOCK,
        "shake_gestures:gestures"
      ).also { it.acquire() }
    },
    release = { wakeLock, _ -> wakeLock.release() }
  )
}
