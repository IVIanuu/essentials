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
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.inject

class ComponentNotificationListenerService : EsNotificationListenerService() {

    private val components: Set<NotificationComponent> by inject(name = NotificationComponents)

    override fun modules(): List<Module> = super.modules() + listOf(notificationComponentsModule)

    override fun onListenerConnected() {
        super.onListenerConnected()
        components.forEach { it.onServiceConnected(this) }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        components.reversed().forEach { it.onServiceDisconnected() }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        components.forEach { it.onNotificationPosted(sbn) }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        components.forEach { it.onNotificationRemoved(sbn) }
    }

    override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
        super.onNotificationRankingUpdate(rankingMap)
        components.forEach { it.onNotificationRankingUpdate(rankingMap) }
    }

    override fun onListenerHintsChanged(hints: Int) {
        super.onListenerHintsChanged(hints)
        components.forEach { it.onListenerHintsChanged(hints) }
    }

}