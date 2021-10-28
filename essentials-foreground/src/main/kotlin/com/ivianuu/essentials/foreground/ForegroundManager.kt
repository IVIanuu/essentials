package com.ivianuu.essentials.foreground

import android.app.Notification
import android.content.Intent
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ForegroundManager {
  suspend fun startForeground(id: Int, notification: StateFlow<Notification>): Nothing
}

suspend fun ForegroundManager.startForeground(id: Int, notification: Notification): Nothing =
  startForeground(id, MutableStateFlow(notification))

@Provide @Scoped<AppComponent> class ForegroundManagerImpl(
  private val context: AppContext,
  private val logger: Logger
) : ForegroundManager {
  private val lock = Mutex()

  private val _states = MutableStateFlow<List<ForegroundState>>(emptyList())
  internal val states: Flow<List<ForegroundState>> get() = _states

  override suspend fun startForeground(
    id: Int,
    notification: StateFlow<Notification>
  ): Nothing = bracket(
    acquire = {
      lock.withLock {
        ForegroundState(id, notification)
          .also {
            _states.value = _states.value + it
            log { "start foreground $id ${_states.value}" }
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
      // we ensure that the foreground service has seen this foreground request
      // to prevent a crash in the android system
      state.seen.await()

      lock.withLock {
        _states.value = _states.value - state
        log { "stop foreground ${state.id} ${_states.value}" }
      }
    }
  )

  internal class ForegroundState(
    val id: Int,
    val notification: StateFlow<Notification>
  ) {
    val seen = CompletableDeferred<Unit>()
  }
}
