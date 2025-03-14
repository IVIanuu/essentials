package essentials.util

import android.os.*
import androidx.compose.runtime.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.coroutines.*
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.*

@Composable fun WakeLock(id: String, scope: Scope<*> = inject) {
  LaunchedEffect(id) {
    withContext(coroutineContexts().io) {
      bracketCase(
        acquire = { systemService<PowerManager>().newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, id) },
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
