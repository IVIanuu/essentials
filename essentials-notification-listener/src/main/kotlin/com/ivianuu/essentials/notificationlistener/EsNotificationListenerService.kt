/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.notificationlistener

import android.service.notification.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

class EsNotificationListenerService : NotificationListenerService() {
  private val _notifications = MutableStateFlow<List<StatusBarNotification>>(emptyList())
  internal val notifications: Flow<List<StatusBarNotification>> by this::_notifications

  private val component: EsNotificationListenerServiceComponent by lazy {
    createServiceScope().element()
  }

  @Provide private val logger get() = component.logger

  private var notificationScope: NotificationScope? = null

  override fun onListenerConnected() {
    super.onListenerConnected()
    d { "listener connected" }
    notificationScope = component.notificationScopeFactory()
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
    d { "ranking update $rankingMap" }
    updateNotifications()
  }

  override fun onListenerDisconnected() {
    d { "listener disconnected" }
    notificationScope?.cast<DisposableScope>()?.dispose()
    notificationScope = null
    component.serviceScope.cast<DisposableScope>().dispose()
    super.onListenerDisconnected()
  }

  private fun updateNotifications() {
    _notifications.value = catch { activeNotifications!!.toList() }
      .getOrElse { emptyList() }
  }
}

@Provide @ScopeElement<ServiceScope>
class EsNotificationListenerServiceComponent(
  val logger: Logger,
  val notificationScopeFactory: () -> NotificationScope,
  val serviceScope: ServiceScope
)
