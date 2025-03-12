package essentials.util

import android.os.*
import androidx.compose.runtime.*
import essentials.*
import injekt.*

@Tag typealias IsPowerSaveMode = Boolean

@Provide @Composable fun isPowerSaveMode(
  broadcastManager: BroadcastManager,
  powerManager: @SystemService PowerManager
): IsPowerSaveMode =
  broadcastManager.broadcastState(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
    powerManager.isPowerSaveMode
  }
