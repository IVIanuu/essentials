/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.app.*
import android.service.notification.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide class NotificationRepository(private val appScope: Scope<AppScope>) {
  val notificationEvents: Flow<NotificationEvent> =
    snapshotFlow { appScope.scopeOfOrNull<NotificationScope>() }
      .flatMapLatest { it?.notificationListenerService?.events ?: emptyFlow() }

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

sealed interface NotificationEvent {
  data class NotificationPosted(val sbn: StatusBarNotification) : NotificationEvent
  data class NotificationRemoved(val sbn: StatusBarNotification) : NotificationEvent
  data class RankingUpdate(val map: NotificationListenerService.RankingMap) : NotificationEvent
}
