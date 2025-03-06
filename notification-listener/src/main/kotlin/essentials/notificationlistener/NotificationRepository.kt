/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.notificationlistener

import android.app.*
import android.service.notification.*
import androidx.compose.runtime.*
import essentials.*
import injekt.*
import kotlinx.coroutines.flow.*

@Stable @Provide class NotificationRepository(private val appScope: Scope<AppScope>) {
  val notifications: List<StatusBarNotification>
    get() = appScope.scopeOfOrNull<NotificationScope>()
      ?.notificationListenerService?.notifications ?: emptyList()

  suspend fun openNotification(notification: Notification) =
    catch { notification.contentIntent.send() }

  suspend fun dismissNotification(key: String) = catch {
    appScope.scopeOf<NotificationScope>().first()
      .notificationListenerService.cancelNotification(key)
  }

  suspend fun dismissAllNotifications() = catch {
    appScope.scopeOf<NotificationScope>().first()
      .notificationListenerService.cancelAllNotifications()
  }
}
