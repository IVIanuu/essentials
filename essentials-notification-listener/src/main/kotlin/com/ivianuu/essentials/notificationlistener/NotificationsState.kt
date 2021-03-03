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
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.notificationlistener.NotificationsAction.DismissAllNotifications
import com.ivianuu.essentials.notificationlistener.NotificationsAction.DismissNotification
import com.ivianuu.essentials.notificationlistener.NotificationsAction.OpenNotification
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.store.state
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

data class NotificationsState(
    val isConnected: Boolean = false,
    val notifications: List<StatusBarNotification> = emptyList()
)

sealed class NotificationsAction {
    data class OpenNotification(val notification: Notification) : NotificationsAction()
    data class DismissNotification(val key: String) : NotificationsAction()
    object DismissAllNotifications : NotificationsAction()
}

@Scoped<AppComponent>
@Given
fun notificationState(
    @Given actions: Flow<NotificationsAction>,
    @Given serviceRef: NotificationServiceRef,
    @Given scope: GlobalScope
) = scope.state(NotificationsState()) {
    serviceRef
        .reduce { copy(isConnected = it != null) }
        .launchIn(this)

    serviceRef
        .flatMapLatest { it?.notifications ?: flowOf(emptyList()) }
        .reduce { copy(notifications = it) }
        .launchIn(this)

    actions
        .filterIsInstance<OpenNotification>()
        .onEach { action ->
            runKatching { action.notification.contentIntent.send() }
        }
        .launchIn(this)

    serviceRef
        .filterNotNull()
        .flatMapLatest { service ->
            actions
                .filterIsInstance<DismissNotification>()
                .map { it.key to service }
        }
        .onEach { (key, service) -> service.cancelNotification(key) }
        .launchIn(this)

    serviceRef
        .filterNotNull()
        .flatMapLatest { service ->
            actions
                .filterIsInstance<DismissAllNotifications>()
                .map { service }
        }
        .onEach { it.cancelAllNotifications() }
        .launchIn(this)
}

@Scoped<AppComponent>
@Given
val notificationsActions get() = EventFlow<NotificationsAction>()
