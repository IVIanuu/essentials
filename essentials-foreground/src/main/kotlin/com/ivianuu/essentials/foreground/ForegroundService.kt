package com.ivianuu.essentials.foreground

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ServiceScope
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.android.createServiceScope
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.ScopeElement
import com.ivianuu.injekt.scope.requireElement
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ForegroundService : Service() {
  private val component: ForegroundServiceComponent by lazy {
    requireElement(createServiceScope())
  }
  @Provide private val logger: Logger get() = component.logger

  private var previousStates = emptyList<Pair<Int, Notification>>()

  override fun onCreate() {
    super.onCreate()
    log { "start foreground service" }

    component.coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
      guarantee(
        block = {
          component.foregroundManager.states
            .flatMapLatest { states ->
              if (states.isNotEmpty()) {
                combine(
                  states
                    .map { state ->
                      state.notification
                        .map { state.id to it }
                    }
                ) { it.toList() }
                  .onStart { emit(states.map { it.id to it.notification.value }) }
              } else flowOf(emptyList())
            }
            .collect { applyState(it) }
        },
        finalizer = {
          applyState(emptyList())
        }
      )
    }
  }

  override fun onDestroy() {
    log { "stop foreground service" }
    component.serviceScope.dispose()
    super.onDestroy()
  }

  private fun applyState(states: List<Pair<Int, Notification>>) {
    log { "apply states: $states" }

    previousStates
      .filter { state -> states.none { state.first == it.first } }
      .forEach { component.notificationManager.cancel(it.first) }

    if (states.isNotEmpty()) {
      states
        .forEachIndexed { index, state ->
          if (index == 0) {
            startForeground(state.first, state.second)
          } else {
            component.notificationManager.notify(state.first, state.second)
          }
        }
    } else {
      stopForeground(true)
      stopSelf()
    }

    previousStates = states
  }

  override fun onBind(intent: Intent?): IBinder? = null
}

@Provide @ScopeElement<ServiceScope>
class ForegroundServiceComponent(
  val coroutineScope: NamedCoroutineScope<ServiceScope>,
  val foregroundManager: ForegroundManager,
  val notificationManager: @SystemService NotificationManager,
  val logger: Logger,
  val serviceScope: ServiceScope
)
