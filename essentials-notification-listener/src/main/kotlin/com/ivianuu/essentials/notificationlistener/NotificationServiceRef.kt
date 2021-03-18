package com.ivianuu.essentials.notificationlistener

import android.app.Service
import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.app.ScopeInitializer
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import kotlinx.coroutines.flow.MutableStateFlow

internal typealias NotificationServiceRef = MutableStateFlow<EsNotificationListenerService?>

@Scoped<AppComponent>
@Given
fun notificationServiceRef(): NotificationServiceRef = MutableStateFlow(null)

@Given
fun notificationServiceRefWorker(
    @Given ref: NotificationServiceRef,
    @Given service: Service
): ScopeWorker<NotificationComponent> = {
    ref.value = service as EsNotificationListenerService
    runOnCancellation { ref.value = null }
}
