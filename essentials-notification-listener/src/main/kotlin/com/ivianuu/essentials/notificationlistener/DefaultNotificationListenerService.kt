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

import android.service.notification.StatusBarNotification
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.get
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DefaultNotificationListenerService : EsNotificationListenerService() {

    private val _notifications = MutableStateFlow<List<StatusBarNotification>>(emptyList())
    val notifications: Flow<List<StatusBarNotification>> by this::_notifications

    private val component by lazy {
        serviceComponent.get<DefaultNotificationListenerServiceComponent>()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        connectedScope.launch(start = CoroutineStart.UNDISPATCHED) {
            runOnCancellation {
                component.logger.d { "listener disconnected" }
                component.notificationServiceRef.value = null
            }
        }
        connectedScope.launch {
            component.logger.d { "listener connected" }
            component.notificationServiceRef.value = this@DefaultNotificationListenerService
            updateNotifications()
            component.notificationWorkerRunner()
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        component.logger.d { "notification posted $sbn" }
        updateNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        component.logger.d { "notification removed $sbn" }
        updateNotifications()
    }

    override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
        super.onNotificationRankingUpdate(rankingMap)
        component.logger.d { "ranking update $rankingMap" }
        updateNotifications()
    }

    private fun updateNotifications() {
        _notifications.value = activeNotifications.toList()
    }
}

@ComponentElementBinding<ServiceComponent>
@Given
class DefaultNotificationListenerServiceComponent(
    @Given val notificationServiceRef: NotificationServiceRef,
    @Given val logger: Logger,
    @Given val notificationWorkerRunner: NotificationWorkerRunner
)

internal typealias NotificationServiceRef = MutableStateFlow<DefaultNotificationListenerService?>

@Scoped<AppComponent>
@Given
fun notificationServiceRef(): NotificationServiceRef = MutableStateFlow(null)
