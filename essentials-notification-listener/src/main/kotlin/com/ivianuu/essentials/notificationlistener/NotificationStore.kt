package com.ivianuu.essentials.notificationlistener

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.ivianuu.essentials.util.DefaultDispatcher
import com.ivianuu.injekt.ImplBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface NotificationStore {
    val isConnected: Flow<Boolean>

    val notifications: StateFlow<List<StatusBarNotification>>

    suspend fun openNotification(notification: Notification)

    suspend fun dismissNotification(key: String)

    suspend fun dismissAllNotifications()
}

@ImplBinding(ApplicationComponent::class)
class NotificationStoreImpl(
    private val defaultDispatcher: DefaultDispatcher,
) : NotificationStore {

    private val service = MutableStateFlow<DefaultNotificationListenerService?>(null)
    override val isConnected: Flow<Boolean> get() = service.map { it != null }

    private val _notifications = MutableStateFlow(emptyList<StatusBarNotification>())
    override val notifications: StateFlow<List<StatusBarNotification>> get() = _notifications

    override suspend fun openNotification(notification: Notification) =
        withContext(defaultDispatcher) {
            service.first { it != null }
            try {
                notification.contentIntent.send()
            } catch (t: Throwable) {
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
