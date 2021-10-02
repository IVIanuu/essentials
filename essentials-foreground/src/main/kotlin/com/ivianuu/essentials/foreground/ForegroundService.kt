package com.ivianuu.essentials.foreground

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.foreground.ForegroundState.Background
import com.ivianuu.essentials.foreground.ForegroundState.Foreground
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class ForegroundService : Service() {
  private val component: ForegroundServiceComponent by lazy {
    requireElement(createServiceScope())
  }
  @Provide private val logger: Logger get() = component.logger

  override fun onCreate() {
    super.onCreate()
    log { "started foreground service" }

    component.coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
      guarantee(
        block = {
          component.internalForegroundState
            .map { it.infos }
            .takeWhile { infos -> infos.any { info -> info.state is Foreground } }
            .collect { applyState(it) }
        },
        finalizer = {
          applyState(emptyList())
        }
      )
    }
  }

  override fun onDestroy() {
    component.serviceScope.dispose()
    super.onDestroy()
  }

  private fun applyState(infos: List<ForegroundInfo>) {
    log { "apply infos: $infos" }

    infos
      .filter { it.state is Background }
      .forEach { component.notificationManager.cancel(it.id) }

    if (infos.any { it.state is Foreground }) {
      infos
        .filter { it.state is Foreground }
        .map { it.id to (it.state as Foreground).notification }
        .forEachIndexed { index, (id, notification) ->
          if (index == 0) {
            startForeground(id, notification)
          } else {
            component.notificationManager.notify(id, notification)
          }
        }
    } else {
      stopForeground(true)
      stopSelf()
    }
  }

  override fun onBind(intent: Intent?): IBinder? = null
}

@Provide @ScopeElement<ServiceScope>
class ForegroundServiceComponent(
  val coroutineScope: NamedCoroutineScope<ServiceScope>,
  val internalForegroundState: Flow<InternalForegroundState>,
  val notificationManager: @SystemService NotificationManager,
  val logger: Logger,
  val serviceScope: ServiceScope
)
