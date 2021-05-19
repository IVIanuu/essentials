package com.ivianuu.essentials.notificationlistener

import android.app.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

@Provide
val notificationListenerRef: @Scoped<AppScope> MutableStateFlow<EsNotificationListenerService?>
  get() = MutableStateFlow(null)

@Provide fun notificationServiceRefWorker(
  ref: MutableStateFlow<EsNotificationListenerService?>,
  service: Service
): ScopeWorker<NotificationScope> = {
  ref.value = service as EsNotificationListenerService
  runOnCancellation { ref.value = null }
}
