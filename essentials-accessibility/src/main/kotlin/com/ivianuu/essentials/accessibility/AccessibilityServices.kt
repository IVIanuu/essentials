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

package com.ivianuu.essentials.accessibility

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.injekt.ImplBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface AccessibilityServices {
    val isConnected: Flow<Boolean>

    val events: Flow<AccessibilityEvent>

    fun applyConfig(config: AccessibilityConfig): DisposableHandle

    suspend fun performGlobalAction(action: Int): Boolean
}

@ImplBinding(ApplicationComponent::class)
class AccessibilityServicesImpl : AccessibilityServices {

    private val service = MutableStateFlow<DefaultAccessibilityService?>(null)
    override val isConnected: Flow<Boolean> get() = service.map { it != null }

    private val _events = EventFlow<AccessibilityEvent>()
    override val events: Flow<AccessibilityEvent> get() = _events

    internal val configs = mutableListOf<AccessibilityConfig>()

    override fun applyConfig(config: AccessibilityConfig): DisposableHandle {
        synchronized(configs) { configs += config }
        updateServiceConfig()
        return object : DisposableHandle {
            override fun dispose() {
                synchronized(this) { configs -= config }
                updateServiceConfig()
            }
        }
    }

    override suspend fun performGlobalAction(action: Int): Boolean =
        service.first { it != null }!!.performGlobalAction(action)

    internal fun onServiceConnected(service: DefaultAccessibilityService) {
        this.service.value = service
        updateServiceConfig()
    }

    internal fun onAccessibilityEvent(event: AndroidAccessibilityEvent) {
        _events.emit(
            AccessibilityEvent(
                type = event.eventType,
                packageName = event.packageName?.toString(),
                className = event.className?.toString(),
                isFullScreen = event.isFullScreen
            )
        )
    }

    internal fun onServiceDisconnected() {
        service.value = null
    }

    private fun updateServiceConfig() {
        val configs = synchronized(configs) { configs }
        service.value?.updateConfig(configs)
    }
}
