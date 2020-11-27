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
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.android.createServiceComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * Base accessibility service
 */
abstract class EsAccessibilityService : AccessibilityService() {

    val serviceComponent by lazy { createServiceComponent() }

    private val component by lazy {
        serviceComponent.mergeComponent<EsAccessibilityServiceComponent>()
    }

    val scope by lazy { CoroutineScope(component.defaultDispatcher) }

    private var _connectedScope: CoroutineScope? = null
    val connectedScope: CoroutineScope get() = _connectedScope ?: error("Not connected")

    override fun onServiceConnected() {
        super.onServiceConnected()
        _connectedScope = CoroutineScope(component.defaultDispatcher)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        _connectedScope?.cancel()
        _connectedScope = null
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
    }
}

@MergeInto(ServiceComponent::class)
interface EsAccessibilityServiceComponent {
    val defaultDispatcher: DefaultDispatcher
}
