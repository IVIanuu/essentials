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
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

typealias Notifications = List<StatusBarNotification>

@Provide fun notifications(ref: Flow<EsNotificationListenerService?>): Flow<Notifications> = ref
  .flatMapLatest { it?.notifications ?: flowOf(emptyList()) }

typealias OpenNotificationUseCase = suspend (Notification) -> Result<Unit, Throwable>

@Provide val openNotificationUseCase: OpenNotificationUseCase = { notification ->
  catch { notification.contentIntent.send() }
}

typealias DismissNotificationUseCase = suspend (String) -> Result<Unit, Throwable>

@Provide fun dismissNotificationUseCase(
  ref: Flow<EsNotificationListenerService?>
): DismissNotificationUseCase = { key ->
  catch { ref.first()!!.cancelNotification(key) }
}

typealias DismissAllNotificationsUseCase = suspend () -> Result<Unit, Throwable>

@Provide fun dismissAllNotificationsUseCase(
  ref: Flow<EsNotificationListenerService?>
): DismissAllNotificationsUseCase = {
  catch { ref.first()!!.cancelAllNotifications() }
}
