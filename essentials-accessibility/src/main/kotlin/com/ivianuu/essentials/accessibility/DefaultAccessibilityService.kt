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
import com.ivianuu.essentials.componentElementBinding
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenGroup
import com.ivianuu.injekt.android.ServiceScoped
import com.ivianuu.injekt.component.Component
import com.ivianuu.injekt.component.get
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class DefaultAccessibilityService : EsAccessibilityService() {

    private val dependencies by lazy {
        serviceComponent[DefaultAccessibilityServiceDependencies]
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        dependencies.logger.d { "connected" }
        dependencies.serviceHolder.value = this
        connectedScope.launch(start = CoroutineStart.UNDISPATCHED) {
            runOnCancellation {
                dependencies.logger.d { "disconnected" }
                dependencies.serviceHolder.value = null
            }
        }
        connectedScope.launch {
            dependencies.runAccessibilityWorkers()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        dependencies.logger.d { "on accessibility event $event" }
        dependencies.accessibilityEvents.emit(
            AccessibilityEvent(
                type = event.eventType,
                packageName = event.packageName?.toString(),
                className = event.className?.toString(),
                isFullScreen = event.isFullScreen
            )
        )
    }

}

@Given class DefaultAccessibilityServiceDependencies(
    @Given val accessibilityEvents: MutableAccessibilityEvents,
    @Given val logger: Logger,
    @Given val runAccessibilityWorkers: runAccessibilityWorkers,
    @Given val serviceHolder: MutableAccessibilityServiceHolder
) {
    companion object : Component.Key<DefaultAccessibilityServiceDependencies> {
        @GivenGroup val binding = componentElementBinding(ServiceScoped, this)
    }
}
