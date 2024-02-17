/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.service.notification.*
import arrow.core.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

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
