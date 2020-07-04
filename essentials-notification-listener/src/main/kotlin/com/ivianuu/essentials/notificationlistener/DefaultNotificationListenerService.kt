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
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.composition.runReader
import com.ivianuu.injekt.get
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class DefaultNotificationListenerService : EsNotificationListenerService() {

    private val logger: Logger by lazy {
        component.runReader { get() }
    }
    private val store: NotificationStore by lazy {
        component.runReader { get() }
    }
    private val workers: Map<KClass<*>, @Provider () -> NotificationWorker> by lazy {
        component.runReader { get() }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        logger.d("listener connected")
        store.onServiceConnected(this)
        notifyUpdate()
        workers.forEach { (key, worker) ->
            connectedScope.launch {
                logger.d("run worker ${key.java.name}")
                scope.launch { worker().run() }
            }
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        logger.d("listener disconnected")
        store.onServiceDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        logger.d("notification posted $sbn")
        notifyUpdate()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        logger.d("notification removed $sbn")
        notifyUpdate()
    }

    override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
        super.onNotificationRankingUpdate(rankingMap)
        logger.d("ranking update $rankingMap")
        notifyUpdate()
    }

    private fun notifyUpdate() {
        store.onNotificationsChanged(activeNotifications.toList())
    }
}
