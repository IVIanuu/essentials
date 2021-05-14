package com.ivianuu.essentials.notificationlistener

import android.app.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

@Given
val notificationListenerRef: @Scoped<AppGivenScope> MutableStateFlow<EsNotificationListenerService?>
  get() = MutableStateFlow(null)

@Given fun notificationServiceRefWorker(
  @Given ref: MutableStateFlow<EsNotificationListenerService?>,
  @Given service: Service
): ScopeWorker<NotificationGivenScope> = {
  ref.value = service as EsNotificationListenerService
  runOnCancellation { ref.value = null }
}
