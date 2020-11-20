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

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.coroutines.collectIn
import com.ivianuu.essentials.notificationlistener.NotificationsAction.*
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.store.GlobalStateBinding
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

data class NotificationsState(
    val isConnected: Boolean = false,
    val notifications: List<StatusBarNotification> = emptyList()
)

sealed class NotificationsAction {
    data class OpenNotification(val notification: Notification) : NotificationsAction()
    data class DismissNotification(val key: String) : NotificationsAction()
    object DismissAllNotifications : NotificationsAction()
}

@GlobalStateBinding
fun NotificationsStore(
    actions: Actions<NotificationsAction>,
    serviceRef: NotificationServiceRef,
    scope: GlobalScope
) = scope.state(NotificationsState()) {
    serviceRef
        .reduce { copy(isConnected = it != null) }

    serviceRef
        .flatMapLatest { it?.notifications ?: flowOf(emptyList()) }
        .reduce { copy(notifications = it) }

    actions
        .filterIsInstance<OpenNotification>()
        .collectIn(this) { action ->
            try {
                action.notification.contentIntent.send()
            } catch (e: Throwable) {
            }
        }

    serviceRef
        .filterNotNull()
        .flatMapLatest { service ->
            actions
                .filterIsInstance<DismissNotification>()
                .map { it.key to service }
        }
        .collectIn(this) { (key, service) -> service.cancelNotification(key) }

    serviceRef
        .filterNotNull()
        .flatMapLatest { service ->
            actions
                .filterIsInstance<DismissAllNotifications>()
                .map { service }
        }
        .collectIn(this) { it.cancelAllNotifications() }
}
