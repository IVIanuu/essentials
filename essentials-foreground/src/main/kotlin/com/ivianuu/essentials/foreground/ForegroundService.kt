package com.ivianuu.essentials.foreground

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.android.createServiceComponent
import com.ivianuu.injekt.common.EntryPoint
import com.ivianuu.injekt.common.dispose
import com.ivianuu.injekt.common.entryPoint
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ForegroundService : Service() {
  private val component: ForegroundServiceComponent by lazy {
    createServiceComponent().entryPoint()
  }
  @Provide private val logger: Logger get() = component.logger

  private var previousStates = emptyList<ForegroundManagerImpl.ForegroundState>()

  override fun onCreate() {
    super.onCreate()
    log { "start foreground service" }

    component.scope.launch(start = CoroutineStart.UNDISPATCHED) {
      guarantee(
        block = {
          component.foregroundManager.states
            .flatMapLatest { states ->
              if (states.isEmpty()) flowOf(emptyList())
              else combine(states.map { it.notification }) { states }
            }
            .collect { applyState(it) }
        },
        finalizer = { applyState(emptyList()) }
      )
    }
  }

  override fun onDestroy() {
    log { "stop foreground service" }
    component.dispose()
    super.onDestroy()
  }

  private fun applyState(states: List<ForegroundManagerImpl.ForegroundState>) {
    log { "apply states: $states" }

    previousStates
      .filter { state -> states.none { state.id == it.id } }
      .forEach { component.notificationManager.cancel(it.id) }

    if (states.isNotEmpty()) {
      states
        .forEachIndexed { index, state ->
          if (index == 0) {
            startForeground(state.id, state.notification.value)
          } else {
            component.notificationManager.notify(state.id, state.notification.value)
          }
        }
    } else {
      stopForeground(true)
      stopSelf()
    }

    states.forEach { it.seen.complete(Unit) }

    previousStates = states
  }

  override fun onBind(intent: Intent?): IBinder? = null
}

@EntryPoint<ServiceComponent> interface ForegroundServiceComponent {
  val foregroundManager: ForegroundManagerImpl
  val notificationManager: @SystemService NotificationManager
  val logger: Logger
  val scope: ComponentScope<ServiceComponent>
}
