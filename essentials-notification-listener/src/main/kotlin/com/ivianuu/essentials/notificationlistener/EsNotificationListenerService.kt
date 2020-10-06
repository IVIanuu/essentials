/*
 * Copyright 2019 Manuel Wrage
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
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.android.createServiceComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * Base notification listener service
 */
abstract class EsNotificationListenerService : NotificationListenerService() {

    val serviceComponent by lazy { createServiceComponent() }

    private val component by lazy {
        serviceComponent.mergeComponent<EsNotificationListenerServiceComponent>()
    }

    val scope by lazy {
        CoroutineScope(component.dispatchers.default)
    }

    private var _connectedScope: CoroutineScope? = null
    val connectedScope: CoroutineScope get() = _connectedScope ?: error("Not connected")

    override fun onListenerConnected() {
        super.onListenerConnected()
        _connectedScope = CoroutineScope(component.dispatchers.default)
    }

    override fun onListenerDisconnected() {
        _connectedScope!!.cancel()
        super.onListenerDisconnected()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun getActiveNotifications(): Array<StatusBarNotification> {
        return try {
            super.getActiveNotifications() ?: emptyArray()
        } catch (t: Throwable) {
            emptyArray()
        }
    }
}

@MergeInto(ServiceComponent::class)
interface EsNotificationListenerServiceComponent {
    val dispatchers: AppCoroutineDispatchers
}
