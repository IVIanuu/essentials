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

import android.app.*
import android.service.notification.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

typealias Notifications = List<StatusBarNotification>

@Given
fun notifications(@Given ref: Flow<EsNotificationListenerService?>): Flow<Notifications> = ref
  .flatMapLatest { it?.notifications ?: flowOf(emptyList()) }

typealias OpenNotificationUseCase = suspend (Notification) -> Result<Unit, Throwable>

@Given val openNotificationUseCase: OpenNotificationUseCase = { notification ->
  catch { notification.contentIntent.send() }
}

typealias DismissNotificationUseCase = suspend (String) -> Result<Unit, Throwable>

@Given fun dismissNotificationUseCase(
  @Given ref: Flow<EsNotificationListenerService?>
): DismissNotificationUseCase = { key ->
  catch { ref.first() !!.cancelNotification(key) }
}

typealias DismissAllNotificationsUseCase = suspend () -> Result<Unit, Throwable>

@Given fun dismissAllNotificationsUseCase(
  @Given ref: Flow<EsNotificationListenerService?>
): DismissAllNotificationsUseCase = {
  catch { ref.first() !!.cancelAllNotifications() }
}
