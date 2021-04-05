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
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.updateIn
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

data class NotificationServiceState(
    val isConnected: Boolean = false,
    val notifications: List<StatusBarNotification> = emptyList()
) : State()

@Scoped<AppGivenScope>
@Given
class NotificationService(
    @Given private val listenerServiceRef: NotificationListenerServiceRef,
    @Given private val store: ScopeStateStore<AppGivenScope, NotificationServiceState>
) : StateFlow<NotificationServiceState> by store {
    init {
        listenerServiceRef
            .updateIn(store) { copy(isConnected = it != null) }
        listenerServiceRef
            .flatMapLatest { it?.notifications ?: flowOf(emptyList()) }
            .updateIn(store) { copy(notifications = it) }
    }
    fun openNotification(notification: Notification) = store.effect {
        runCatching { notification.contentIntent.send() }
    }
    fun dismissNotification(key: String) = store.effect {
        listenerServiceRef.value?.cancelNotification(key)
    }
    fun dismissAllNotifications() = store.effect {
        listenerServiceRef.value?.cancelAllNotifications()
    }
}
