/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.notificationlistener

import android.app.*
import android.service.notification.*
import androidx.compose.runtime.*
import essentials.*
import injekt.*

sealed interface NotificationApi {
  data object Unavailable : NotificationApi
  data object Empty : NotificationApi
  data class Notifications(
    val notifications: List<StatusBarNotification>,
    val openNotification: suspend (Notification) -> Boolean,
    val dismissNotification: suspend (String) -> Unit,
    val dismissAllNotifications: suspend () -> Unit
  ) : NotificationApi
}

@Provide @Composable fun notificationApi(
  appScope: Scope<AppScope>
): @ComposeIn<AppScope> NotificationApi {
  val notificationListenerService = appScope.scopeOfOrNull<NotificationScope>()
    ?.notificationListenerService
  return if (notificationListenerService == null) NotificationApi.Unavailable
  else if (notificationListenerService.notifications.isEmpty()) NotificationApi.Empty
  else NotificationApi.Notifications(
    notifications = notificationListenerService.notifications,
    openNotification = { catch { it.contentIntent.send() }.fold({ false }, { true }) },
    dismissNotification = notificationListenerService::cancelNotification,
    dismissAllNotifications = notificationListenerService::cancelAllNotifications
  )
}
