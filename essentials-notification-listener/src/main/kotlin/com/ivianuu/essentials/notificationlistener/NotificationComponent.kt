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

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.ivianuu.essentials.app.AppComponentHolder
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

abstract class NotificationComponent {

    var service: ComponentNotificationListenerService? = null
        private set

    val coroutineScope by unsafeLazy {
        CoroutineScope(
            Job() + AppComponentHolder.get<AppCoroutineDispatchers>().computation
        )
    }

    open fun onServiceConnected(service: ComponentNotificationListenerService) {
        this.service = service
    }

    open fun onNotificationPosted(sbn: StatusBarNotification) {
    }

    open fun onNotificationRemoved(sbn: StatusBarNotification) {
    }

    open fun onNotificationRankingUpdate(rankingMap: NotificationListenerService.RankingMap) {
    }

    open fun onListenerHintsChanged(hints: Int) {
    }

    open fun onServiceDisconnected() {
        coroutineScope.cancel()
        service = null
    }
}
