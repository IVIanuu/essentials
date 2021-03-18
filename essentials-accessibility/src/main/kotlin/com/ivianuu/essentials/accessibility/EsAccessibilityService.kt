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

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.android.createServiceComponent
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.element
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EsAccessibilityService : AccessibilityService() {

    private val component by lazy {
        createServiceComponent()
            .element<EsAccessibilityServiceComponent>()
    }

    private var accessibilityComponent: AccessibilityComponent? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        component.logger.d { "connected" }
        accessibilityComponent = component.accessibilityComponentFactory()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        component.logger.d { "on accessibility event $event" }
        component.accessibilityEvents.tryEmit(
            AccessibilityEvent(
                type = event.eventType,
                packageName = event.packageName?.toString(),
                className = event.className?.toString(),
                isFullScreen = event.isFullScreen
            )
        )
    }

    override fun onInterrupt() {
    }

    override fun onUnbind(intent: Intent?): Boolean {
        component.logger.d { "disconnected" }
        accessibilityComponent?.dispose()
        accessibilityComponent = null
        component.serviceComponent.dispose()
        return super.onUnbind(intent)
    }

}

@ComponentElementBinding<ServiceComponent>
@Given
class EsAccessibilityServiceComponent(
    @Given val accessibilityEvents: MutableAccessibilityEvents,
    @Given val accessibilityComponentFactory: () -> AccessibilityComponent,
    @Given val logger: Logger,
    @Given val serviceComponent: ServiceComponent
)
