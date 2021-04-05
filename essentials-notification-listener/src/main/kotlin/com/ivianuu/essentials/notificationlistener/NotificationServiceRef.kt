package com.ivianuu.essentials.notificationlistener

import android.app.Service
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.MutableStateFlow

internal typealias NotificationListenerServiceRef = MutableStateFlow<EsNotificationListenerService?>

@Given
fun notificationListenerServiceRef(): @Scoped<AppGivenScope> NotificationListenerServiceRef =
    MutableStateFlow(null)

@Given
fun notificationServiceListenerRefWorker(
    @Given ref: NotificationListenerServiceRef,
    @Given service: Service
): ScopeWorker<NotificationGivenScope> = {
    ref.value = service as EsNotificationListenerService
    runOnCancellation { ref.value = null }
}
