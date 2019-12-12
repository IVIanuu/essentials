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

package com.ivianuu.essentials.foreground

import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.Scope
import java.util.concurrent.atomic.AtomicInteger

abstract class ForegroundComponent {

    abstract val notificationFactory: NotificationFactory
    val id = ids.incrementAndGet()

    var manager: ForegroundManager? = null
        private set

    private val _scope = ReusableScope()
    val scope: Scope get() = _scope

    open fun attach(manager: ForegroundManager) {
        this.manager = manager
    }

    open fun detach() {
        _scope.close()
        this.manager = null
    }

    protected fun stopForeground() {
        manager?.stopForeground(this)
    }

    protected fun updateNotification() {
        manager?.startForeground(this)
    }
}

private val ids = AtomicInteger(0)
