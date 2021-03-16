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
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.util.addFlag
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Qualifier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

data class AccessibilityConfig(
    val eventTypes: Int = 0,
    val flags: Int = 0,
    val packageNames: Set<String>? = null,
    val feedbackType: Int = AccessibilityServiceInfo.FEEDBACK_GENERIC,
    val notificationTimeout: Long = 0L,
)

@Given
fun applyAccessibilityConfig(
    @Given configs: Set<() -> Flow<AccessibilityConfig>> = emptySet(),
    @Given serviceHolder: MutableAccessibilityServiceHolder,
): AccessibilityWorker = {
    coroutineScope {
        serviceHolder
            .flatMapLatest { service ->
                if (service != null) {
                    combine(
                        configs
                            .map { it() }
                            .map { config ->
                                config
                                    .stateIn(this, SharingStarted.Eagerly, null)
                            }
                    ) { it.filterNotNull() }
                        .map { service to it }
                } else {
                    infiniteEmptyFlow()
                }
            }
            .onEach { (service, configs) ->
                service.serviceInfo = service.serviceInfo?.apply {
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
            .collect()
    }
}
