/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.app.*
import android.service.notification.*
import kotlinx.coroutines.flow.*

interface NotificationService {
  val notifications: Flow<List<StatusBarNotification>>

  val events: Flow<NotificationEvent>

  suspend fun openNotification(notification: Notification)

  suspend fun dismissNotification(key: String)

  suspend fun dismissAllNotifications()
}

@Provide class NotificationServiceImpl(
  private val ref: Flow<EsNotificationListenerService?>
) : NotificationService {
  override val notifications: Flow<List<StatusBarNotification>>
    get() = ref
      .flatMapLatest { it?.notifications ?: flowOf(emptyList()) }

  override val events: Flow<NotificationEvent>
    get() = ref
      .flatMapLatest { it?.events ?: emptyFlow() }

  override suspend fun openNotification(notification: Notification) {
    runCatching { notification.contentIntent.send() }
  }

  override suspend fun dismissNotification(key: String) {
    runCatching { ref.first()!!.cancelNotification(key) }
  }

  override suspend fun dismissAllNotifications() {
    runCatching { ref.first()!!.cancelAllNotifications() }
  }
}
