package essentials.util

import android.app.*
import android.os.*
import androidx.compose.runtime.*
import androidx.core.content.*
import injekt.*

@Tag typealias IsPowerSaveMode = Boolean

@Provide @Composable fun isPowerSaveMode(
  broadcasts: Broadcasts,
  context: Application = inject
): IsPowerSaveMode =
  broadcasts.stateOf(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
    context.getSystemService<PowerManager>()!!.isPowerSaveMode
  }
