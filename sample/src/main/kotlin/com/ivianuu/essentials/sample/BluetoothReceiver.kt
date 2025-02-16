package com.ivianuu.essentials.sample

import android.bluetooth.*
import androidx.compose.runtime.*
import arrow.fx.coroutines.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.seconds

@Provide class BluetoothReceiver(
  private val broadcastManager: BroadcastManager,
  private val logger: Logger
) : ScopeComposition<AppScope> {
  @Composable override fun Content() {
    if (!broadcastManager.broadcasts(BluetoothDevice.ACTION_ACL_CONNECTED)
      .map { it.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)!! }
      .transformLatest {
        guaranteeCase(
          fa = {
            emit(true)
            logger.d { "apply configs broadcast" }
            var i = 0
            while (true) {
              i++
              delay(1.seconds)
              logger.d { "apply configs $i" }
              if (i > 10) break
            }
            logger.d { "stop apply configs broadcast" }
            emit(false)
          },
          finalizer = {
            logger.d { "finalize apply configs $it" }
          }
        )
      }.collectAsState(false).value
    )
      return
  }
}
