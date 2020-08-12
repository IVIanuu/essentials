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
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.injekt.ApplicationStorage
import com.ivianuu.injekt.Given
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

@Given(ApplicationStorage::class)
class AccessibilityServices {

    private val _service = MutableStateFlow<DefaultAccessibilityService?>(null)
    val service: StateFlow<DefaultAccessibilityService?> get() = _service

    val isConnected: Boolean get() = _service.value != null

    private val _events = EventFlow<AccessibilityEvent>()
    val events: Flow<AccessibilityEvent> get() = _events

    internal val configs = mutableListOf<AccessibilityConfig>()

    fun applyConfig(config: AccessibilityConfig): DisposableHandle {
        synchronized(configs) { configs += config }
        updateServiceConfig()
        return object : DisposableHandle {
            override fun dispose() {
                synchronized(this) { configs -= config }
                updateServiceConfig()
            }
        }
    }

    suspend fun performGlobalAction(action: Int): Boolean =
        service.first { it != null }!!.performGlobalAction(action)

    internal fun onServiceConnected(service: DefaultAccessibilityService) {
        _service.value = service
        updateServiceConfig()
    }

    internal fun onAccessibilityEvent(event: AccessibilityEvent) {
        _events.offer(event)
    }

    internal fun onServiceDisconnected() {
        _service.value = null
    }

    private fun updateServiceConfig() {
        val configs = synchronized(configs) { configs }
        service.value?.updateConfig(configs)
    }

}
