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
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import com.ivianuu.injekt.runReader
import kotlinx.coroutines.launch

class DefaultNotificationListenerService : EsNotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        readerContext.runReader {
            d { "listener connected" }
            given<NotificationStore>().onServiceConnected(this)
            given<NotificationWorkers>().forEach { worker ->
                connectedScope.launch {
                    scope.launch { worker() }
                }
            }
            notifyUpdate()
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        readerContext.runReader {
            d { "listener disconnected" }
            given<NotificationStore>().onServiceDisconnected()
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        readerContext.runReader {
            d { "notification posted $sbn" }
            notifyUpdate()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        readerContext.runReader {
            d { "notification removed $sbn" }
            notifyUpdate()
        }
    }

    override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
        super.onNotificationRankingUpdate(rankingMap)
        readerContext.runReader {
            d { "ranking update $rankingMap" }
            notifyUpdate()
        }
    }

    @Reader
    private fun notifyUpdate() {
        given<NotificationStore>().onNotificationsChanged(activeNotifications.toList())
    }
}
