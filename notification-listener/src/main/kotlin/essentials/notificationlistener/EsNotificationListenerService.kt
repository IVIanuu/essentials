/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.notificationlistener

import android.service.notification.*
import androidx.compose.runtime.*
import arrow.core.*
import essentials.*
import essentials.app.AndroidComponent
import essentials.coroutines.*
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide @AndroidComponent class EsNotificationListenerService(
  private val logger: Logger,
  private val notificationScopeFactory: (@Service<NotificationScope> EsNotificationListenerService) -> Scope<NotificationScope>
) : NotificationListenerService() {
  var notifications by mutableStateOf(emptyList<StatusBarNotification>())
    private set

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
  }

  override fun onNotificationRemoved(sbn: StatusBarNotification) {
    super.onNotificationRemoved(sbn)
    logger.d { "notification removed $sbn" }
    updateNotifications()
  }

  override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
    super.onNotificationRankingUpdate(rankingMap)
    logger.d { "notification ranking update $rankingMap" }
    updateNotifications()
  }

  override fun onListenerDisconnected() {
    logger.d { "notification listener disconnected" }
    notificationScope?.dispose()
    notificationScope = null
    super.onListenerDisconnected()
  }

  private fun updateNotifications() {
    notifications = catch { activeNotifications!!.toList() }
      .printErrors()
      .getOrElse { emptyList() }
  }
}
