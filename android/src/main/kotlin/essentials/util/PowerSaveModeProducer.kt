package essentials.util

import android.os.*
import androidx.compose.runtime.*
import essentials.*
import injekt.*

@Tag typealias IsPowerSaveMode = Boolean

@Provide @Composable fun isPowerSaveMode(
  broadcasts: Broadcasts,
  powerManager: @SystemService PowerManager
): IsPowerSaveMode =
  broadcasts.stateOf(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
    powerManager.isPowerSaveMode
  }
