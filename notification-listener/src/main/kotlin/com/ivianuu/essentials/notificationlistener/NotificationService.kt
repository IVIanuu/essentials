/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import arrow.core.Either
import com.ivianuu.essentials.ScopeManager
import com.ivianuu.essentials.scopeOf
import com.ivianuu.essentials.scopeOfOrNull
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

@Provide class NotificationService(private val scopeManager: ScopeManager) {
  val notificationEvents: Flow<NotificationEvent> =
    scopeManager.scopeOfOrNull<NotificationScope>()
      .flatMapLatest { it?.notificationListenerService?.events ?: emptyFlow() }

  val notifications: Flow<List<StatusBarNotification>> =
    scopeManager.scopeOfOrNull<NotificationScope>()
      .flatMapLatest { it?.notificationListenerService?.notifications ?: emptyFlow() }

  suspend fun openNotification(notification: Notification) =
    Either.catch { notification.contentIntent.send() }

  suspend fun dismissNotification(key: String) = Either.catch {
    scopeManager.scopeOf<NotificationScope>().first()
      .notificationListenerService.cancelNotification(key)
  }

  suspend fun dismissAllNotifications() = Either.catch {
    scopeManager.scopeOf<NotificationScope>().first()
      .notificationListenerService.cancelAllNotifications()
  }
}

sealed interface NotificationEvent {
  data class NotificationPosted(val sbn: StatusBarNotification) : NotificationEvent
  data class NotificationRemoved(val sbn: StatusBarNotification) : NotificationEvent
  data class RankingUpdate(val map: NotificationListenerService.RankingMap) : NotificationEvent
}
