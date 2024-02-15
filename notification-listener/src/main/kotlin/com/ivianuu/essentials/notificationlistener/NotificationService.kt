/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import arrow.core.Either
import com.ivianuu.essentials.ScopeManager
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.scopeOf
import com.ivianuu.essentials.scopeOfOrNull
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

interface NotificationService {
  val notificationEvents: Flow<NotificationEvent>
  val notifications: Flow<List<StatusBarNotification>>

  suspend fun openNotification(notification: Notification): Either<Throwable, Unit>

  suspend fun dismissNotification(key: String): Either<Throwable, Unit>

  suspend fun dismissAllNotifications(): Either<Throwable, Unit>
}

@Provide class NotificationServiceImpl(
  private val scopeManager: ScopeManager
) : NotificationService {
  override val notificationEvents: Flow<NotificationEvent> =
    scopeManager.scopeOfOrNull<NotificationScope>()
      .flatMapLatest { it?.notificationListenerService?.events ?: emptyFlow() }

  override val notifications: Flow<List<StatusBarNotification>> =
    scopeManager.scopeOfOrNull<NotificationScope>()
      .flatMapLatest { it?.notificationListenerService?.notifications ?: emptyFlow() }

  override suspend fun openNotification(notification: Notification) =
    Either.catch { notification.contentIntent.send() }

  override suspend fun dismissNotification(key: String) = Either.catch {
    scopeManager.scopeOf<NotificationScope>().first()
      .notificationListenerService.cancelNotification(key)
  }

  override suspend fun dismissAllNotifications() = Either.catch {
    scopeManager.scopeOf<NotificationScope>().first()
      .notificationListenerService.cancelAllNotifications()
  }
}

sealed interface NotificationEvent {
  data class NotificationPosted(val sbn: StatusBarNotification) : NotificationEvent
  data class NotificationRemoved(val sbn: StatusBarNotification) : NotificationEvent
  data class RankingUpdate(val map: NotificationListenerService.RankingMap) : NotificationEvent
}
