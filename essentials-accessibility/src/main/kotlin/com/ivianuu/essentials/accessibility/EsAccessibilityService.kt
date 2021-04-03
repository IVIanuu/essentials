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
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ServiceGivenScope
import com.ivianuu.injekt.android.createServiceGivenScope
import com.ivianuu.injekt.scope.GivenScopeElementBinding

class EsAccessibilityService : AccessibilityService() {
    private val component by lazy {
        createServiceGivenScope()
            .element<EsAccessibilityServiceComponent>()
    }

    private var accessibilityGivenScope: AccessibilityGivenScope? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        component.logger.d { "connected" }
        accessibilityGivenScope = component.accessibilityGivenScopeFactory()
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
        accessibilityGivenScope?.dispose()
        accessibilityGivenScope = null
        component.serviceGivenScope.dispose()
        return super.onUnbind(intent)
    }
}

@GivenScopeElementBinding<ServiceGivenScope>
@Given
class EsAccessibilityServiceComponent(
    @Given val accessibilityEvents: MutableAccessibilityEvents,
    @Given val accessibilityGivenScopeFactory: () -> AccessibilityGivenScope,
    @Given val logger: Logger,
    @Given val serviceGivenScope: ServiceGivenScope
)
