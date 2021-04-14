package com.ivianuu.essentials.notificationlistener

import android.app.Service
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.MutableStateFlow

@Given
val notificationListenerRef: @Scoped<AppGivenScope> MutableStateFlow<EsNotificationListenerService?>
    get() = MutableStateFlow(null)

@Given
fun notificationServiceRefWorker(
    @Given ref: MutableStateFlow<EsNotificationListenerService?>,
    @Given service: Service
): ScopeWorker<NotificationGivenScope> = {
    ref.value = service as EsNotificationListenerService
    runOnCancellation { ref.value = null }
}
