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
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.Scope

/**
 * Base notification listener service
 */
abstract class EsNotificationListenerService : NotificationListenerService(), InjektTrait {

    override val component by unsafeLazy {
        ServiceComponent {
            modules(this@EsNotificationListenerService.modules())
        }
    }

    private val _scope = MutableScope()
    val scope: Scope get() = _scope

    private val _connectedScope = ReusableScope()
    val connectedScope: Scope get() = _connectedScope

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onListenerDisconnected() {
        _connectedScope.clear()
        super.onListenerDisconnected()
    }

    override fun getActiveNotifications(): Array<StatusBarNotification> {
        return try {
            super.getActiveNotifications() ?: emptyArray()
        } catch (e: Exception) {
            emptyArray()
        }
    }

    protected open fun modules(): List<Module> = emptyList()
}
