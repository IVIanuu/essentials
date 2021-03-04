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

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.result.getOrElse
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.android.createServiceComponent
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EsNotificationListenerService : NotificationListenerService() {

    private val _notifications = MutableStateFlow<List<StatusBarNotification>>(emptyList())
    internal val notifications: Flow<List<StatusBarNotification>> by this::_notifications

    private val component by lazy {
        createServiceComponent()
            .get<EsNotificationListenerServiceComponent>()
    }

    private var connectedScope: CoroutineScope? = null

    override fun onListenerConnected() {
        super.onListenerConnected()
        val connectedScope = CoroutineScope(component.defaultDispatcher)
            .also { this.connectedScope = it }
        connectedScope.launch {
            component.logger.d { "listener connected" }
            component.notificationServiceRef.value = this@EsNotificationListenerService
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

    override fun onListenerDisconnected() {
        component.logger.d { "listener disconnected" }
        connectedScope?.cancel()
        connectedScope = null
        component.serviceComponent.dispose()
        component.notificationServiceRef.value = null
        super.onListenerDisconnected()
    }

    private fun updateNotifications() {
        _notifications.value =  runKatching { activeNotifications!!.toList() }
            .getOrElse { emptyList() }
    }

}

@ComponentElementBinding<ServiceComponent>
@Given
class EsNotificationListenerServiceComponent(
    @Given val defaultDispatcher: DefaultDispatcher,
    @Given val notificationServiceRef: NotificationServiceRef,
    @Given val logger: Logger,
    @Given val notificationWorkerRunner: NotificationWorkerRunner,
    @Given val serviceComponent: ServiceComponent
)

internal typealias NotificationServiceRef = MutableStateFlow<EsNotificationListenerService?>

@Scoped<AppComponent>
@Given
fun notificationServiceRef(): NotificationServiceRef = MutableStateFlow(null)
