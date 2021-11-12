/*
 * Copyright 2021 Manuel Wrage
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

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.onSuccess
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.android.createServiceComponent
import com.ivianuu.injekt.common.EntryPoint
import com.ivianuu.injekt.common.dispose
import com.ivianuu.injekt.common.entryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class EsNotificationListenerService : NotificationListenerService() {
  private val _notifications = mutableStateListOf<StatusBarNotification>()
  val notifications: List<StatusBarNotification> get() = _notifications

  private val _events: MutableSharedFlow<NotificationEvent> = EventFlow()
  val events: Flow<NotificationEvent> get() = _events

  private val component: EsNotificationListenerServiceComponent by lazy {
    createServiceComponent().entryPoint()
  }

  @Provide private val logger get() = component.logger

  private var notificationComponent: NotificationComponent? = null

  override fun onListenerConnected() {
    super.onListenerConnected()
    log { "listener connected" }
    notificationComponent = component.notificationComponentFactory.notificationComponent()
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
    notificationComponent?.dispose()
    notificationComponent = null
    component.dispose()
    component.notificationServiceRef.value = null
    super.onListenerDisconnected()
  }

  private fun updateNotifications() {
    _notifications.clear()
    catch { activeNotifications!!.toList() }
      .onSuccess { _notifications.addAll(it) }
  }
}

sealed class NotificationEvent {
  data class NotificationPosted(val sbn: StatusBarNotification) : NotificationEvent()
  data class NotificationRemoved(val sbn: StatusBarNotification) : NotificationEvent()
  data class RankingUpdate(val map: NotificationListenerService.RankingMap) : NotificationEvent()
}

@EntryPoint<ServiceComponent> interface EsNotificationListenerServiceComponent {
  val logger: Logger
  val notificationComponentFactory: NotificationComponentFactory
  val notificationServiceRef: MutableState<EsNotificationListenerService?>
}
