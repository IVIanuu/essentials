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

import android.accessibilityservice.AccessibilityServiceInfo
import com.ivianuu.essentials.tuples.combine
import com.ivianuu.essentials.util.addFlag
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

data class AccessibilityConfig(
    val eventTypes: Int = 0,
    val flags: Int = 0,
    val packageNames: Set<String>? = null,
    val feedbackType: Int = AccessibilityServiceInfo.FEEDBACK_GENERIC,
    val notificationTimeout: Long = 0L
)

internal typealias AccessibilityConfigs = MutableStateFlow<List<AccessibilityConfig>>
@Binding(ApplicationComponent::class)
fun accessibilityConfigs(): AccessibilityConfigs = MutableStateFlow(emptyList())

typealias applyAccessibilityConfig = (AccessibilityConfig) -> DisposableHandle
@Binding
fun applyAccessibilityConfig(configs: AccessibilityConfigs): applyAccessibilityConfig = { config ->
    synchronized(configs) { configs.value += config }
    object : DisposableHandle {
        override fun dispose() {
            synchronized(configs) { configs.value -= config }
        }
    }
}

@AccessibilityWorkerBinding
fun manageAccessibilityConfig(
    configs: AccessibilityConfigs,
    serviceHolder: MutableAccessibilityServiceHolder
): AccessibilityWorker = {
    combine(serviceHolder, configs)
        .collect { (service, configs) ->
            service?.serviceInfo = service?.serviceInfo?.apply {
                eventTypes = configs
                    .map { it.eventTypes }
                    .fold(0) { acc, events -> acc.addFlag(events) }

                flags = configs
                    .map { it.flags }
                    .fold(0) { acc, flags -> acc.addFlag(flags) }

                // first one wins
                configs.firstOrNull()?.feedbackType?.let { feedbackType = it }
                feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

                notificationTimeout = configs
                    .map { it.notificationTimeout }
                    .maxOrNull() ?: 0L

                packageNames = null
            }
        }
}
