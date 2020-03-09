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

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.app.ComponentBuilderInterceptor
import com.ivianuu.essentials.util.addFlag
import com.ivianuu.injekt.getLazy

class ComponentAccessibilityService : EsAccessibilityService(), ComponentBuilderInterceptor {

    private val components: Set<AccessibilityComponent> by getLazy(qualifier = AccessibilityComponents)

    override fun onServiceConnected() {
        super.onServiceConnected()

        d { "initialize with components $components" }
        components.forEach { it.onServiceConnected(this) }

        updateServiceInfo()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        d { "on accessibility event $event" }
        components
            .filter { it.config.packageNames == null || event.packageName in it.config.packageNames!! }
            .forEach { it.onAccessibilityEvent(event) }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        d { "on unbind" }
        components.reversed().forEach { it.onServiceDisconnected() }
        return super.onUnbind(intent)
    }

    fun updateServiceInfo() {
        serviceInfo = serviceInfo.apply {
            val configurations = components.map { it.config }

            eventTypes = configurations
                .map { it.eventTypes }
                .fold(0) { acc, events -> acc.addFlag(events) }

            flags = configurations
                .map { it.flags }
                .fold(0) { acc, flags -> acc.addFlag(flags) }

            configurations.lastOrNull()?.feedbackType?.let { feedbackType = it } // todo
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

            notificationTimeout = configurations
                .map { it.notificationTimeout }
                .max() ?: 0L

            packageNames = null

            d { "update service info $this" }
        }
    }
}
