package essentials.util

import android.os.*
import androidx.compose.runtime.*
import essentials.*
import injekt.*

@Provide class PowerSaveModeProducer(
  private val broadcastManager: BroadcastManager,
  private val powerManager: @SystemService PowerManager
) {
  @Composable fun isPowerSaveMode(): Boolean =
    produceState(remember { powerManager.isPowerSaveMode }) {
      broadcastManager.broadcasts(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        .collect { value = powerManager.isPowerSaveMode }
    }.value
}
