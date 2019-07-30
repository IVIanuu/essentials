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

package com.ivianuu.essentials.gestures.accessibility

import android.view.accessibility.AccessibilityEvent
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.gestures.esGesturesModule
import com.ivianuu.injekt.inject

abstract class EsComponentAccessibilityService : EsAccessibilityService() {

    private val components by inject<Set<AccessibilityComponent>>()

    override fun modules() = super.modules() + listOf(esGesturesModule)

    override fun onServiceConnected() {
        super.onServiceConnected()
        d { "initialize with components $components" }
        components.forEach { it.onServiceConnected(this) }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        d { "on accessibility event $event" }
        components.forEach { it.onAccessibilityEvent(event) }
    }

    override fun onDestroy() {
        d { "on destroy" }
        components.forEach { it.onServiceDisconnected() }
        super.onDestroy()
    }

}