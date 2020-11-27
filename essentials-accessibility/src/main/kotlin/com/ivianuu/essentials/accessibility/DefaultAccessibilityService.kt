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

import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class DefaultAccessibilityService : EsAccessibilityService() {

    private val component by lazy {
        serviceComponent.mergeComponent<DefaultAccessibilityServiceComponent>()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        component.logger.d { "connected" }
        component.serviceHolder.value = this
        connectedScope.launch(start = CoroutineStart.UNDISPATCHED) {
            runOnCancellation {
                component.logger.d { "disconnected" }
                component.serviceHolder.value = null
            }
        }
        connectedScope.launch {
            component.runAccessibilityWorkers()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        component.logger.d { "on accessibility event $event" }
        component.accessibilityEvents.emit(
            AccessibilityEvent(
                type = event.eventType,
                packageName = event.packageName?.toString(),
                className = event.className?.toString(),
                isFullScreen = event.isFullScreen
            )
        )
    }

}

@MergeInto(ServiceComponent::class)
interface DefaultAccessibilityServiceComponent {
    val accessibilityEvents: MutableAccessibilityEvents
    val logger: Logger
    val runAccessibilityWorkers: runAccessibilityWorkers
    val serviceHolder: MutableAccessibilityServiceHolder
}
