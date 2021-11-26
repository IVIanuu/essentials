package com.ivianuu.essentials.foreground

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.state.composedFlow
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.android.createServiceComponent
import com.ivianuu.injekt.common.Component
import com.ivianuu.injekt.common.ComponentElement
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ForegroundService : Service() {
  private val component by lazy {
    createServiceComponent().element<ForegroundServiceComponent>()
  }
  @Provide val logger get() = component.logger

  private var previousStates = emptyList<Pair<ForegroundManagerImpl.ForegroundState, Notification>>()

  override fun onCreate() {
    super.onCreate()
    log { "start foreground service" }

    component.scope.launch(start = CoroutineStart.UNDISPATCHED) {
      guarantee(
        block = {
          composedFlow {
            component.foregroundManager.states
              .map { it to it.notification() }
          }
            .collect { applyState(it) }
        },
        finalizer = { applyState(emptyList()) }
      )
    }
  }

  override fun onDestroy() {
    log { "stop foreground service" }
    component.component.dispose()
    super.onDestroy()
  }

  private fun applyState(states: List<Pair<ForegroundManagerImpl.ForegroundState, Notification>>) {
    log { "apply states: $states" }

    previousStates
      .filter { state -> states.none { state.first.id == it.first.id } }
      .forEach { component.notificationManager.cancel(it.first.id) }

    if (states.isNotEmpty()) {
      states
        .forEachIndexed { index, (state, notification) ->
          if (index == 0) {
            startForeground(state.id, notification)
          } else {
            component.notificationManager.notify(state.id, notification)
          }
        }
    } else {
      stopForeground(true)
      stopSelf()
    }

    states.forEach { it.first.seen.complete(Unit) }

    previousStates = states
  }

  override fun onBind(intent: Intent?): IBinder? = null
}

@Provide @ComponentElement<ServiceComponent> data class ForegroundServiceComponent(
  val foregroundManager: ForegroundManagerImpl,
  val notificationManager: @SystemService NotificationManager,
  val logger: Logger,
  val scope: ComponentScope<ServiceComponent>,
  val component: Component<ServiceComponent>
)
