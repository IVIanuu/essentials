/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.github.michaelbull.result.getOrElse
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class EsNotificationListenerService : NotificationListenerService() {
  private val _notifications = MutableStateFlow(emptyList<StatusBarNotification>())
  val notifications: Flow<List<StatusBarNotification>> get() = _notifications

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
    _notifications.value = catch { activeNotifications!!.toList() }
      .getOrElse { emptyList() }
  }

  companion object {
    @Provide val notificationListenerRef = MutableStateFlow<EsNotificationListenerService?>(null)
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
  val notificationServiceRef: MutableStateFlow<EsNotificationListenerService?>
)
