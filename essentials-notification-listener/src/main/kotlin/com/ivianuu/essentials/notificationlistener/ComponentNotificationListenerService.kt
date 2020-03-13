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

package com.ivianuu.essentials.notificationlistener

import android.service.notification.StatusBarNotification
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.getLazy

class ComponentNotificationListenerService : EsNotificationListenerService() {

    private val components: Set<NotificationComponent> by getLazy(qualifier = NotificationComponents)
    private val logger: Logger by getLazy()

    override fun ComponentBuilder.buildComponent() {
        notificationComponentInjection()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        logger.d("initialize with components $components")
        components.forEach { it.onServiceConnected(this) }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        logger.d("disconnected")
        components.reversed().forEach { it.onServiceDisconnected() }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        logger.d("notification posted $sbn")
        components.forEach { it.onNotificationPosted(sbn) }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        logger.d("notification removed $sbn")
        components.forEach { it.onNotificationRemoved(sbn) }
    }

    override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
        super.onNotificationRankingUpdate(rankingMap)
        logger.d("ranking update $rankingMap")
        components.forEach { it.onNotificationRankingUpdate(rankingMap) }
    }

    override fun onListenerHintsChanged(hints: Int) {
        super.onListenerHintsChanged(hints)
        logger.d("hints changed $hints")
        components.forEach { it.onListenerHintsChanged(hints) }
    }
}
