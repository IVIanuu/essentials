package com.ivianuu.essentials.foreground

import android.app.Notification
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ForegroundManager {
  suspend fun startForeground(id: Int, notification: @Composable () -> Notification): Nothing
}

suspend fun ForegroundManager.startForeground(id: Int, notification: Notification): Nothing =
  startForeground(id) { notification }

@Provide @Scoped<AppComponent> class ForegroundManagerImpl(
  private val context: AppContext,
  private val L: Logger
) : ForegroundManager {
  private val lock = Mutex()

  internal var states = mutableStateListOf<ForegroundState>()
    private set

  override suspend fun startForeground(
    id: Int,
    notification: @Composable () -> Notification
  ): Nothing = bracket(
    acquire = {
      lock.withLock {
        ForegroundState(id, notification)
          .also {
            states += it
            log { "start foreground $id $states" }
          }
      }
    },
    use = {
      ContextCompat.startForegroundService(
        context,
        Intent(context, ForegroundService::class.java)
      )

      awaitCancellation()
    },
    release = { state, _ ->
      // we ensure that the foreground se#rvice has seen this foreground request
      // to prevent a crash in the android system
      state.seen.await()

      lock.withLock {
        states -= state
        log { "stop foreground ${state.id} $states" }
      }
    }
  )

  internal class ForegroundState(val id: Int, val notification: @Composable () -> Notification) {
    val seen = CompletableDeferred<Unit>()
  }
}
