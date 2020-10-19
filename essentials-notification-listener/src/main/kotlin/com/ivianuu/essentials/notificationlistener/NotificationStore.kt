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
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.BindingModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface NotificationStore {
    val isConnected: Flow<Boolean>

    val notifications: StateFlow<List<StatusBarNotification>>

    suspend fun openNotification(notification: Notification): Boolean

    suspend fun dismissNotification(key: String)

    suspend fun dismissAllNotifications()
}

@Binding
val NotificationStoreImpl.notificationStore: NotificationStore
    get() = this

@Binding(ApplicationComponent::class)
class NotificationStoreImpl(
    private val defaultDispatcher: DefaultDispatcher,
) : NotificationStore {

    private val service = MutableStateFlow<DefaultNotificationListenerService?>(null)
    override val isConnected: Flow<Boolean> get() = service.map { it != null }

    private val _notifications = MutableStateFlow(emptyList<StatusBarNotification>())
    override val notifications: StateFlow<List<StatusBarNotification>> get() = _notifications

    override suspend fun openNotification(notification: Notification): Boolean {
        return withContext(defaultDispatcher) {
            service.first { it != null }
            try {
                notification.contentIntent.send()
                true
            } catch (t: Throwable) {
                false
            }
        }
    }

    override suspend fun dismissNotification(key: String): Unit = withContext(defaultDispatcher) {
        service.first { it != null }!!.cancelNotification(key)
    }

    override suspend fun dismissAllNotifications(): Unit = withContext(defaultDispatcher) {
        service.first { it != null }!!.cancelAllNotifications()
    }

    internal fun onServiceConnected(service: DefaultNotificationListenerService) {
        this.service.value = service
    }

    internal fun onNotificationsChanged(notifications: List<StatusBarNotification>) {
        _notifications.value = notifications
    }

    internal fun onServiceDisconnected() {
        this.service.value = null
    }
}
