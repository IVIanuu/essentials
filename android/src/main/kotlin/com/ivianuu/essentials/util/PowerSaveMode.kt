package com.ivianuu.essentials.util

import android.os.PowerManager
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@JvmInline value class PowerSaveMode(val value: Boolean) {
  @Provide companion object {
    @Provide fun powerSaveMode(
      broadcastsFactory: BroadcastsFactory,
      powerManager: @SystemService PowerManager,
      scope: ScopedCoroutineScope<AppScope>,
    ): @Scoped<AppScope> StateFlow<PowerSaveMode> =
      broadcastsFactory(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        .map { PowerSaveMode(powerManager.isPowerSaveMode) }
        .stateIn(
          scope,
          SharingStarted.WhileSubscribed(),
          PowerSaveMode(powerManager.isPowerSaveMode)
        )
  }
}
