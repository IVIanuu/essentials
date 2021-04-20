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

import android.service.notification.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

class EsNotificationListenerService : NotificationListenerService() {
    private val _notifications = MutableStateFlow<List<StatusBarNotification>>(emptyList())
    internal val notifications: Flow<List<StatusBarNotification>> by this::_notifications

    private val component by lazy {
        createServiceGivenScope()
            .element<EsNotificationListenerServiceComponent>()
    }

    private var notificationGivenScope: NotificationGivenScope? = null

    override fun onListenerConnected() {
        super.onListenerConnected()
        component.logger.d { "listener connected" }
        notificationGivenScope = component.notificationGivenScopeFactory()
        updateNotifications()
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
        notificationGivenScope?.dispose()
        notificationGivenScope = null
        component.serviceGivenScope.dispose()
        super.onListenerDisconnected()
    }

    private fun updateNotifications() {
        _notifications.value = catch { activeNotifications!!.toList() }
            .getOrElse { emptyList() }
    }
}

@InstallElement<ServiceGivenScope>
@Given
class EsNotificationListenerServiceComponent(
    @Given val logger: Logger,
    @Given val notificationGivenScopeFactory: () -> NotificationGivenScope,
    @Given val serviceGivenScope: ServiceGivenScope
)
