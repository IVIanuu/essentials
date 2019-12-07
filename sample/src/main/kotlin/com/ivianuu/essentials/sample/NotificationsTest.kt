/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.sample

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.notificationlistener.ComponentNotificationListenerService
import com.ivianuu.essentials.notificationlistener.NotificationComponent
import com.ivianuu.injekt.Factory

@Factory
class NotificationsTest : NotificationComponent() {

    override fun onServiceConnected(service: ComponentNotificationListenerService) {
        super.onServiceConnected(service)
        d { "on service connected $service" }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        d { "posted $sbn" }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        d { "removed $sbn" }
    }

    override fun onNotificationRankingUpdate(rankingMap: NotificationListenerService.RankingMap) {
        super.onNotificationRankingUpdate(rankingMap)
        d { "on ranking update $rankingMap" }
    }

    override fun onListenerHintsChanged(hints: Int) {
        super.onListenerHintsChanged(hints)
        d { "on hints changed $hints" }
    }

    override fun onServiceDisconnected() {
        super.onServiceDisconnected()
        d { "service disconnected" }
    }

}