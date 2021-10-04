package com.ivianuu.essentials.foreground

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Provide @Scoped<AppScope> class ForegroundManager(
  private val context: AppContext,
  private val logger: Logger
) {

  private val mutex = Mutex()

  private val _states = MutableStateFlow<List<ForegroundState>>(emptyList())
  internal val states: Flow<List<ForegroundState>> get() = _states

  private var currentId = 1

  suspend fun startForeground(notification: StateFlow<Notification>): Nothing = bracket(
    acquire = {
      val state = mutex.withLock {
        val id = currentId++
        ForegroundState(id, notification)
          .also { _states.value = _states.value + it }
      }

      ContextCompat.startForegroundService(
        context,
        Intent(context, ForegroundService::class.java)
      )

      state
    },
    use = { awaitCancellation() },
    release = { state, _ ->
      mutex.withLock {
        _states.value = _states.value - state
      }
    }
  )

  internal class ForegroundState(val id: Int, val notification: StateFlow<Notification>)
}

suspend fun ForegroundManager.startForeground(notification: Notification): Nothing =
  startForeground(MutableStateFlow(notification))
