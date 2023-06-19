/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

interface NotificationService {
  val notifications: Flow<List<StatusBarNotification>>
  val notificationEvents: Flow<NotificationEvent>

  suspend fun openNotification(notification: Notification): Result<Unit, Throwable>

  suspend fun dismissNotification(key: String): Result<Unit, Throwable>

  suspend fun dismissAllNotifications(): Result<Unit, Throwable>
}

@Provide class NotificationServiceImpl(
  private val ref: Flow<EsNotificationListenerService?>
) : NotificationService {
  override val notifications: Flow<List<StatusBarNotification>> =
    ref.flatMapLatest { it?.notifications ?: flowOf(emptyList()) }

  override val notificationEvents: Flow<NotificationEvent> =
    ref.flatMapLatest { it?.events ?: emptyFlow() }

  override suspend fun openNotification(notification: Notification) =
    catch { notification.contentIntent.send() }

  override suspend fun dismissNotification(key: String) =
    catch { ref.first()!!.cancelNotification(key) }

  override suspend fun dismissAllNotifications() =
    catch { ref.first()!!.cancelAllNotifications() }
}

sealed interface NotificationEvent {
  data class NotificationPosted(val sbn: StatusBarNotification) : NotificationEvent
  data class NotificationRemoved(val sbn: StatusBarNotification) : NotificationEvent
  data class RankingUpdate(val map: NotificationListenerService.RankingMap) : NotificationEvent
}
