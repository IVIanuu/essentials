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
import com.github.ajalt.timberkt.d
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.getLazy

class ComponentNotificationListenerService : EsNotificationListenerService() {

    private val components: Set<NotificationComponent> by getLazy(name = NotificationComponents)

    override fun modules(): List<Module> = super.modules() + listOf(NotificationComponentsModule)

    override fun onListenerConnected() {
        super.onListenerConnected()
        d { "initialize with components $components" }
        components.forEach { it.onServiceConnected(this) }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        d { "disconnected" }
        components.reversed().forEach { it.onServiceDisconnected() }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        d { "notification posted $sbn" }
        components.forEach { it.onNotificationPosted(sbn) }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        d { "notification removed $sbn" }
        components.forEach { it.onNotificationRemoved(sbn) }
    }

    override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
        super.onNotificationRankingUpdate(rankingMap)
        d { "ranking update $rankingMap" }
        components.forEach { it.onNotificationRankingUpdate(rankingMap) }
    }

    override fun onListenerHintsChanged(hints: Int) {
        super.onListenerHintsChanged(hints)
        d { "hints changed $hints" }
        components.forEach { it.onListenerHintsChanged(hints) }
    }
}
