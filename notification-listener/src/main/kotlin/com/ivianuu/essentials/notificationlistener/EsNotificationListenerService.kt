/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import arrow.core.Either
import arrow.core.getOrElse
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Provide @AndroidComponent class EsNotificationListenerService(
  private val logger: Logger,
  private val notificationScopeFactory: (@Service<NotificationScope> EsNotificationListenerService) -> Scope<NotificationScope>
) : NotificationListenerService() {
  private val _notifications = MutableStateFlow(emptyList<StatusBarNotification>())
  internal val notifications: StateFlow<List<StatusBarNotification>> by this::_notifications

  private val _events: MutableSharedFlow<NotificationEvent> = EventFlow()
  internal val events: Flow<NotificationEvent> by this::_events

  private var notificationScope: Scope<NotificationScope>? = null

  override fun onListenerConnected() {
    super.onListenerConnected()
    logger.log { "listener connected" }
    notificationScope = notificationScopeFactory(this)
    updateNotifications()
  }

  override fun onNotificationPosted(sbn: StatusBarNotification) {
    super.onNotificationPosted(sbn)
    logger.log { "notification posted $sbn" }
    updateNotifications()
    _events.tryEmit(NotificationEvent.NotificationPosted(sbn))
  }

  override fun onNotificationRemoved(sbn: StatusBarNotification) {
    super.onNotificationRemoved(sbn)
    logger.log { "notification removed $sbn" }
    updateNotifications()
    _events.tryEmit(NotificationEvent.NotificationRemoved(sbn))
  }

  override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
    super.onNotificationRankingUpdate(rankingMap)
    logger.log { "ranking update $rankingMap" }
    updateNotifications()
    _events.tryEmit(NotificationEvent.RankingUpdate(rankingMap))
  }

  override fun onListenerDisconnected() {
    logger.log { "listener disconnected" }
    notificationScope?.dispose()
    notificationScope = null
    super.onListenerDisconnected()
  }

  private fun updateNotifications() {
    _notifications.value = catch { activeNotifications!!.toList() }
      .getOrElse { emptyList() }
  }
}
