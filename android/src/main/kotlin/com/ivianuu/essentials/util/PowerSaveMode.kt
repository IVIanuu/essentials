package com.ivianuu.essentials.util

import android.os.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.flow.*

@JvmInline value class PowerSaveMode(val value: Boolean) {
  @Provide companion object {
    @Provide fun powerSaveMode(
      broadcastManager: BroadcastManager,
      powerManager: @SystemService PowerManager,
      scope: ScopedCoroutineScope<AppScope>,
    ): @Scoped<AppScope> StateFlow<PowerSaveMode> =
      broadcastManager.broadcasts(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        .map { PowerSaveMode(powerManager.isPowerSaveMode) }
        .stateIn(
          scope,
          SharingStarted.WhileSubscribed(),
          PowerSaveMode(powerManager.isPowerSaveMode)
        )
  }
}
