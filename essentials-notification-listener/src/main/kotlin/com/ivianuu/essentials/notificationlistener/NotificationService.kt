/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.app.*
import android.service.notification.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

interface NotificationService {
  val notifications: List<StatusBarNotification>

  val events: Flow<NotificationEvent>

  suspend fun openNotification(notification: Notification)

  suspend fun dismissNotification(key: String)

  suspend fun dismissAllNotifications()
}

@Provide class NotificationServiceImpl(
  private val ref: State<EsNotificationListenerService?>
) : NotificationService {
  override val notifications: List<StatusBarNotification>
    get() = ref.value?.notifications ?: emptyList()

  override val events: Flow<NotificationEvent>
    get() = snapshotFlow { ref.value }
      .flatMapLatest { it?.events ?: emptyFlow() }

  override suspend fun openNotification(notification: Notification) {
    catch { notification.contentIntent.send() }
  }

  override suspend fun dismissNotification(key: String) {
    catch { ref.value!!.cancelNotification(key) }
  }

  override suspend fun dismissAllNotifications() {
    catch { ref.value!!.cancelAllNotifications() }
  }
}
