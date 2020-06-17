package com.ivianuu.essentials.notificationlistener

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.ApplicationScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

@ApplicationScoped
class NotificationStore(private val dispatchers: AppCoroutineDispatchers) {

    private var service: DefaultNotificationListenerService? = null
        private set

    val isConnected: Boolean get() = service != null

    private val _notifications = MutableStateFlow(emptyList<StatusBarNotification>())
    val notifications: StateFlow<List<StatusBarNotification>> get() = _notifications

    suspend fun openNotification(notification: Notification) =
        withContext(dispatchers.default) {
            check(isConnected)
            try {
                notification.contentIntent.send()
            } catch (e: Exception) {
            }
        }

    suspend fun dismissNotification(key: String) = withContext(dispatchers.default) {
        check(isConnected)
        service?.cancelNotification(key)
    }

    suspend fun dismissAllNotifications() = withContext(dispatchers.default) {
        check(isConnected)
        service?.cancelAllNotifications()
    }

    internal fun onServiceConnected(service: DefaultNotificationListenerService) {
        this.service = service
    }

    internal fun onNotificationsChanged(notifications: List<StatusBarNotification>) {
        _notifications.value = notifications
    }

    internal fun onServiceDisconnected() {
        service = null
    }

}
