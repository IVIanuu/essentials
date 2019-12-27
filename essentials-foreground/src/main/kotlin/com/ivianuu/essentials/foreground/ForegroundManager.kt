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

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.flow.Flow

@ApplicationScope
@Single
class ForegroundManager(private val context: Context) {

    private val _updates = EventFlow<Unit>()
    val updates: Flow<Unit> get() = _updates

    private val _components = mutableListOf<ForegroundComponent>()
    val components: List<ForegroundComponent> get() = _components.toList()

    private val _stopServiceRequests = EventFlow<Unit>()
    internal val stopServiceRequests: Flow<Unit> get() = _stopServiceRequests

    fun startForeground(component: ForegroundComponent) = synchronized(this) {
        if (component in _components) {
            d { "update foreground $component" }
            updateServiceState()
            dispatchUpdate()
            return@synchronized
        }

        d { "start foreground $component" }

        _components += component
        component.attach(this)
        updateServiceState()
        dispatchUpdate()
    }

    fun stopForeground(component: ForegroundComponent) = synchronized(this) {
        if (component !in _components) return@synchronized
        d { "stop foreground $component" }
        component.detach()
        _components -= component
        updateServiceState()
        dispatchUpdate()
    }

    private fun dispatchUpdate() {
        _updates.offer(Unit)
    }

    private fun updateServiceState() = synchronized(this) {
        d { "update service state $_components" }
        if (_components.isNotEmpty()) {
            d { "start foreground service" }
            ContextCompat.startForegroundService(
                context,
                Intent(context, ForegroundService::class.java)
            )
        } else {
            d { "stop foreground service" }
            _stopServiceRequests.offer(Unit)
        }
    }
}
