package essentials.util

import android.os.*
import androidx.compose.runtime.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.coroutines.*
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.*

@Stable @Provide class WakeLockManager(
  private val coroutineContexts: CoroutineContexts,
  @property:Provide private val logger: Logger,
  private val powerManager: @SystemService PowerManager
) {
  @Composable fun WakeLock(id: String) {
    LaunchedEffect(id) {
      withContext(coroutineContexts.io) {
        bracketCase(
          acquire = { powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, id) },
          use = {
            d { "$id acquire wake lock" }
            it.acquire()
            awaitCancellation()
          },
          release = { wakeLock, _ ->
            d { "$id release wake lock" }
            wakeLock.release()
          }
        )
      }
    }
  }
}
