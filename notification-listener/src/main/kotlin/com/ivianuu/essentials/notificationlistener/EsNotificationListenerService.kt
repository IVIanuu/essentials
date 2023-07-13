/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.getOrElse
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

context(Logger) @Provide @AndroidComponent class EsNotificationListenerService(
  private val notificationScopeFactory: () -> Scope<NotificationScope>,
  private val notificationServiceRef: MutableStateFlow<EsNotificationListenerService?>
) : NotificationListenerService() {
  private val _notifications = MutableStateFlow(emptyList<StatusBarNotification>())
  internal val notifications: StateFlow<List<StatusBarNotification>> by this::_notifications

  private val _events: MutableSharedFlow<NotificationEvent> = EventFlow()
  internal val events: Flow<NotificationEvent> by this::_events

  private var notificationScope: Scope<NotificationScope>? = null

  override fun onListenerConnected() {
    super.onListenerConnected()
    log { "listener connected" }
    this.notificationScope = notificationScopeFactory()
    notificationServiceRef.value = this
    updateNotifications()
  }

  override fun onNotificationPosted(sbn: StatusBarNotification) {
    super.onNotificationPosted(sbn)
    log { "notification posted $sbn" }
    updateNotifications()
    _events.tryEmit(NotificationEvent.NotificationPosted(sbn))
  }

  override fun onNotificationRemoved(sbn: StatusBarNotification) {
    super.onNotificationRemoved(sbn)
    log { "notification removed $sbn" }
    updateNotifications()
    _events.tryEmit(NotificationEvent.NotificationRemoved(sbn))
  }

  override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
    super.onNotificationRankingUpdate(rankingMap)
    log { "ranking update $rankingMap" }
    updateNotifications()
    _events.tryEmit(NotificationEvent.RankingUpdate(rankingMap))
  }

  override fun onListenerDisconnected() {
    log { "listener disconnected" }
    notificationScope?.dispose()
    notificationScope = null
    notificationServiceRef.value = null
    super.onListenerDisconnected()
  }

  private fun updateNotifications() {
    _notifications.value = catch { activeNotifications!!.toList() }
      .getOrElse { emptyList() }
  }

  companion object {
    @Provide val notificationListenerRef = MutableStateFlow<EsNotificationListenerService?>(null)
  }
}
