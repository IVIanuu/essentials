/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.app.*
import android.service.notification.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

interface NotificationService {
  val notifications: Flow<List<StatusBarNotification>>
  val events: Flow<NotificationEvent>

  suspend fun openNotification(notification: Notification): EsResult<Unit, Throwable>

  suspend fun dismissNotification(key: String): EsResult<Unit, Throwable>

  suspend fun dismissAllNotifications(): EsResult<Unit, Throwable>
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

  override suspend fun openNotification(notification: Notification) =
    catch { notification.contentIntent.send() }

  override suspend fun dismissNotification(key: String) =
    catch { ref.first()!!.cancelNotification(key) }

  override suspend fun dismissAllNotifications() =
    catch { ref.first()!!.cancelAllNotifications() }
}
