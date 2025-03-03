/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.service.notification.*
import androidx.compose.runtime.*
import arrow.core.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide @AndroidComponent class EsNotificationListenerService(
  private val logger: Logger,
  private val notificationScopeFactory: (@Service<NotificationScope> EsNotificationListenerService) -> Scope<NotificationScope>
) : NotificationListenerService() {
  var notifications by mutableStateOf(emptyList<StatusBarNotification>())
    private set

  private val _events: MutableSharedFlow<NotificationEvent> = EventFlow()
  internal val events: Flow<NotificationEvent> by this::_events

  private var notificationScope: Scope<NotificationScope>? = null

  override fun onListenerConnected() {
    super.onListenerConnected()
    logger.d { "notification listener connected" }
    notificationScope = notificationScopeFactory(this)
    updateNotifications()
  }

  override fun onNotificationPosted(sbn: StatusBarNotification) {
    super.onNotificationPosted(sbn)
    logger.d { "notification posted $sbn" }
    updateNotifications()
    _events.tryEmit(NotificationEvent.NotificationPosted(sbn))
  }

  override fun onNotificationRemoved(sbn: StatusBarNotification) {
    super.onNotificationRemoved(sbn)
    logger.d { "notification removed $sbn" }
    updateNotifications()
    _events.tryEmit(NotificationEvent.NotificationRemoved(sbn))
  }

  override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
    super.onNotificationRankingUpdate(rankingMap)
    logger.d { "notification ranking update $rankingMap" }
    updateNotifications()
    _events.tryEmit(NotificationEvent.RankingUpdate(rankingMap))
  }

  override fun onListenerDisconnected() {
    logger.d { "notification listener disconnected" }
    notificationScope?.dispose()
    notificationScope = null
    super.onListenerDisconnected()
  }

  private fun updateNotifications() {
    notifications = catch { activeNotifications!!.toList() }
      .getOrElse { emptyList() }
  }
}
