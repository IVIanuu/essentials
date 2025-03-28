/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.notificationlistener

import android.service.notification.*
import androidx.compose.runtime.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.app.*
import injekt.*

@Provide @AndroidComponent class EsNotificationListenerService(
  @property:Provide private val logger: Logger,
  private val notificationScopeFactory: (
    @Service<NotificationScope> EsNotificationListenerService
  ) -> @New Scope<NotificationScope>
) : NotificationListenerService() {
  var notifications by mutableStateOf(emptyList<StatusBarNotification>())
    private set

  private var notificationScope: Scope<NotificationScope>? = null

  override fun onListenerConnected() {
    super.onListenerConnected()
    d { "notification listener connected" }
    notificationScope = notificationScopeFactory(this)
    updateNotifications()
  }

  override fun onNotificationPosted(sbn: StatusBarNotification) {
    super.onNotificationPosted(sbn)
    d { "notification posted $sbn" }
    updateNotifications()
  }

  override fun onNotificationRemoved(sbn: StatusBarNotification) {
    super.onNotificationRemoved(sbn)
    d { "notification removed $sbn" }
    updateNotifications()
  }

  override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
    super.onNotificationRankingUpdate(rankingMap)
    d { "notification ranking update $rankingMap" }
    updateNotifications()
  }

  override fun onListenerDisconnected() {
    d { "notification listener disconnected" }
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
