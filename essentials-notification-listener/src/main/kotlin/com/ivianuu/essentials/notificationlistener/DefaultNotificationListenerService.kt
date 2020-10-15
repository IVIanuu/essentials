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
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent
import kotlinx.coroutines.launch

class DefaultNotificationListenerService : EsNotificationListenerService() {

    private val component by lazy {
        serviceComponent.mergeComponent<DefaultNotificationListenerServiceComponent>()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        component.logger.d("listener connected")
        component.notificationStore.onServiceConnected(this)
        component.notificationWorkers.forEach { worker ->
            connectedScope.launch {
                scope.launch { worker() }
            }
        }
        notifyUpdate()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        component.logger.d("listener disconnected")
        component.notificationStore.onServiceDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        component.logger.d("notification posted $sbn")
        notifyUpdate()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        component.logger.d("notification removed $sbn")
        notifyUpdate()
    }

    override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
        super.onNotificationRankingUpdate(rankingMap)
        component.logger.d("ranking update $rankingMap")
        notifyUpdate()
    }

    private fun notifyUpdate() {
        component.notificationStore.onNotificationsChanged(activeNotifications.toList())
    }
}

@MergeInto(ServiceComponent::class)
interface DefaultNotificationListenerServiceComponent {
    val logger: Logger
    val notificationStore: NotificationStoreImpl
    val notificationWorkers: NotificationWorkers
}
