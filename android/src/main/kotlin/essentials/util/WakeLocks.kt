package essentials.util

import android.app.*
import android.os.*
import androidx.compose.runtime.*
import androidx.core.content.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*

@Stable @Provide class WakeLocks(
  private val context: Application,
  private val coroutineContexts: CoroutineContexts,
  @property:Provide private val logger: Logger,
) {
  @Composable fun WakeLock(id: String) {
    LaunchedEffect(id) {
      withContext(coroutineContexts.io) {
        bracketCase(
          acquire = {
            context.getSystemService<PowerManager>()!!
              .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, id)
          },
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
