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
import com.ivianuu.essentials.notificationlistener.NotificationServiceAction.DismissAllNotifications
import com.ivianuu.essentials.notificationlistener.NotificationServiceAction.DismissNotification
import com.ivianuu.essentials.notificationlistener.NotificationServiceAction.OpenNotification
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.onAction
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

data class NotificationServiceState(
    val isConnected: Boolean = false,
    val notifications: List<StatusBarNotification> = emptyList()
)

sealed class NotificationServiceAction {
    data class OpenNotification(val notification: Notification) : NotificationServiceAction()
    data class DismissNotification(val notificationKey: String) : NotificationServiceAction()
    object DismissAllNotifications : NotificationServiceAction()
}

@Given
fun notificationServiceStore(
    @Given listenerServiceRef: NotificationListenerServiceRef
): StoreBuilder<AppGivenScope, NotificationServiceState, NotificationServiceAction> = {
    listenerServiceRef
        .update { copy(isConnected = it != null) }
    listenerServiceRef
        .flatMapLatest { it?.notifications ?: flowOf(emptyList()) }
        .update { copy(notifications = it) }
    onAction<OpenNotification> { action ->
        runCatching { action.notification.contentIntent.send() }
    }
    onAction<DismissNotification> { action ->
        listenerServiceRef.value?.cancelNotification(action.notificationKey)
    }
    onAction<DismissAllNotifications> {
        listenerServiceRef.value?.cancelAllNotifications()
    }
}
