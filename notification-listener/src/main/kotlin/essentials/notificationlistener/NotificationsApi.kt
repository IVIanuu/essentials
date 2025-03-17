/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.notificationlistener

import android.app.*
import android.service.notification.*
import androidx.compose.runtime.*
import com.github.michaelbull.result.*
import essentials.*
import injekt.*

sealed interface NotificationsApi {
  data object Unavailable : NotificationsApi
  data object Empty : NotificationsApi
  data class Notifications(
    val notifications: List<StatusBarNotification>,
    val openNotification: suspend (Notification) -> Boolean,
    val dismissNotification: suspend (String) -> Unit,
    val dismissAllNotifications: suspend () -> Unit
  ) : NotificationsApi
}

@Provide @Composable fun notificationsApi(
  appScope: Scope<AppScope>
): @ComposeIn<AppScope> NotificationsApi {
  val notificationListenerService = appScope.scopeOfOrNull<NotificationScope>()
    ?.notificationListenerService
  return if (notificationListenerService == null) NotificationsApi.Unavailable
  else if (notificationListenerService.notifications.isEmpty()) NotificationsApi.Empty
  else NotificationsApi.Notifications(
    notifications = notificationListenerService.notifications,
    openNotification = { catch { it.contentIntent.send() }.fold({ true }, { false }) },
    dismissNotification = notificationListenerService::cancelNotification,
    dismissAllNotifications = notificationListenerService::cancelAllNotifications
  )
}
