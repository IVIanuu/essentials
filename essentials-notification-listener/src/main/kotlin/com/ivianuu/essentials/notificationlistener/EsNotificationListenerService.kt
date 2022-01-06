/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.service.notification.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

class EsNotificationListenerService : NotificationListenerService() {
  private val _notifications = mutableStateListOf<StatusBarNotification>()
  val notifications: List<StatusBarNotification> get() = _notifications

  private val _events: MutableSharedFlow<NotificationEvent> = EventFlow()
  val events: Flow<NotificationEvent> get() = _events

  private val component by lazy {
    application
      .cast<AppElementsOwner>()
      .appElements<EsNotificationListenerServiceComponent>()
  }

  @Provide private val logger get() = component.logger

  private var notificationScope: Scope<NotificationScope>? = null

  override fun onListenerConnected() {
    super.onListenerConnected()
    log { "listener connected" }
    val scope = Scope<NotificationScope>()
      .also { this.notificationScope = it }
    component.notificationElementsFactory(scope, this)
    component.notificationServiceRef.value = this
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
    component.notificationServiceRef.value = null
    super.onListenerDisconnected()
  }

  private fun updateNotifications() {
    _notifications.clear()
    catch { activeNotifications!!.toList() }
      .onSuccess { _notifications.addAll(it) }
  }
}

sealed interface NotificationEvent {
  data class NotificationPosted(val sbn: StatusBarNotification) : NotificationEvent
  data class NotificationRemoved(val sbn: StatusBarNotification) : NotificationEvent
  data class RankingUpdate(val map: NotificationListenerService.RankingMap) : NotificationEvent
}

@Provide @Element<AppScope>
data class EsNotificationListenerServiceComponent(
  val logger: Logger,
  val notificationElementsFactory: (Scope<NotificationScope>, EsNotificationListenerService) -> Elements<NotificationScope>,
  val notificationServiceRef: MutableState<EsNotificationListenerService?>
)
