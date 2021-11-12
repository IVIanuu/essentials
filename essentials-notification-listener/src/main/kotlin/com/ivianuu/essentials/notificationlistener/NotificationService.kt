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

import android.app.Notification
import android.service.notification.StatusBarNotification
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshotFlow
import com.ivianuu.essentials.catch
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

interface NotificationService {
  val notifications: List<StatusBarNotification>

  val events: Flow<NotificationEvent>

  suspend fun openNotification(notification: Notification)

  suspend fun dismissNotification(key: String)

  suspend fun dismissAllNotifications()
}

@Provide class NotificationServiceImpl(
  private val ref: State<EsNotificationListenerService?>
) : NotificationService {
  override val notifications: List<StatusBarNotification>
    get() = ref.value?.notifications ?: emptyList()

  override val events: Flow<NotificationEvent>
    get() = snapshotFlow { ref.value }
      .flatMapLatest { it?.events ?: emptyFlow() }

  override suspend fun openNotification(notification: Notification) {
    catch { notification.contentIntent.send() }
  }

  override suspend fun dismissNotification(key: String) {
    catch { ref.value!!.cancelNotification(key) }
  }

  override suspend fun dismissAllNotifications() {
    catch { ref.value!!.cancelAllNotifications() }
  }
}
