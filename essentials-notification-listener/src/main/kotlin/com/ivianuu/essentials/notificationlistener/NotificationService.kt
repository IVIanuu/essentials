/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.notificationlistener

import android.app.Notification
import android.app.Service
import android.service.notification.StatusBarNotification
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.util.cast
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

interface NotificationService {
    val state: Flow<NotificationServiceState>
    suspend fun openNotification(notification: Notification)
    suspend fun dismissNotification(notificationKey: String)
    suspend fun dismissAllNotifications()
}

data class NotificationServiceState(
    val isConnected: Boolean = false,
    val notifications: List<StatusBarNotification> = emptyList()
)

@Given
@Scoped<AppGivenScope>
class NotificationServiceImpl(
    @Given private val scope: ScopeCoroutineScope<AppGivenScope>
) : NotificationService {
    private val ref = MutableStateFlow<EsNotificationListenerService?>(null)
    override val state: Flow<NotificationServiceState> = ref
        .flatMapLatest { service ->
            service?.notifications?.map { NotificationServiceState(true, it) }
                ?: flowOf(NotificationServiceState())
        }
        .shareIn(scope, SharingStarted.Lazily, 1)

    override suspend fun openNotification(notification: Notification) {
        runCatching { notification.contentIntent.send() }
    }

    override suspend fun dismissNotification(notificationKey: String) {
        ref.value?.cancelNotification(notificationKey)
    }

    override suspend fun dismissAllNotifications() {
        ref.value?.cancelAllNotifications()
    }

    internal fun updateRef(service: EsNotificationListenerService?) {
        ref.value = service
    }
}

@Given
fun notificationServiceListenerRefWorker(
    @Given androidService: Service,
    @Given service: NotificationServiceImpl
): ScopeWorker<NotificationGivenScope> = {
    service.updateRef(androidService.cast())
    runOnCancellation { service.updateRef(null) }
}
