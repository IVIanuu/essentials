package com.ivianuu.essentials.notificationlistener

import android.app.Service
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.flow.MutableStateFlow

internal typealias NotificationServiceRef = MutableStateFlow<EsNotificationListenerService?>

@Scoped<AppGivenScope>
@Given
fun notificationServiceRef(): NotificationServiceRef = MutableStateFlow(null)

@Given
fun notificationServiceRefWorker(
    @Given ref: NotificationServiceRef,
    @Given service: Service
): ScopeWorker<NotificationGivenScope> = {
    ref.value = service as EsNotificationListenerService
    runOnCancellation { ref.value = null }
}
