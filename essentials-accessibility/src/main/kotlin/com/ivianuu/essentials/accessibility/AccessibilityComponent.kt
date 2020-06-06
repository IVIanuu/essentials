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

package com.ivianuu.essentials.accessibility

import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.util.unsafeLazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

abstract class AccessibilityComponen {

    abstract val config: AccessibilityConfig

    var service: ComponentAccessibilityService? = null
        private set

    val coroutineScope by unsafeLazy {
        CoroutineScope(Job() + Dispatchers.Main)
    }

    open fun onServiceConnected(service: ComponentAccessibilityService) {
        this.service = service
    }

    open fun onAccessibilityEvent(event: AccessibilityEvent) {
    }

    open fun onServiceDisconnected() {
        coroutineScope.cancel()
        service = null
    }

    protected fun updateServiceInfo() {
        service?.updateServiceInfo()
    }
}
