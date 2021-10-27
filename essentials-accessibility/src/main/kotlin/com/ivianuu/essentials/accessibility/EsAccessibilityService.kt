/*
 * Copyright 2021 Manuel Wrage
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
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.addFlag
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.android.createServiceComponent
import com.ivianuu.injekt.common.EntryPoint
import com.ivianuu.injekt.common.dispose
import com.ivianuu.injekt.common.entryPoint
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EsAccessibilityService : AccessibilityService() {
  private val component: EsAccessibilityServiceComponent by lazy {
    createServiceComponent().entryPoint()
  }

  @Provide private val logger get() = component.logger

  private var accessibilityComponent: AccessibilityComponent? = null

  override fun onServiceConnected() {
    super.onServiceConnected()
    log { "service connected" }
    accessibilityComponent = component.accessibilityComponentFactory.accessibilityComponent()
    component.accessibilityServiceRef.value = this

    val accessibilityServiceComponent =
      accessibilityComponent!!.entryPoint<EsAccessibilityServiceAccessibilityComponent>()

    accessibilityServiceComponent.scope.launch(start = CoroutineStart.UNDISPATCHED) {
      log { "update config from ${accessibilityServiceComponent.configs}" }
      serviceInfo = serviceInfo?.apply {
        eventTypes = accessibilityServiceComponent.configs
          .map { it.eventTypes }
          .fold(0) { acc, events -> acc.addFlag(events) }

        flags = accessibilityServiceComponent.configs
          .map { it.flags }
          .fold(0) { acc, flags -> acc.addFlag(flags) }

        // first one wins
        accessibilityServiceComponent.configs.firstOrNull()?.feedbackType?.let { feedbackType = it }
        feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

        notificationTimeout = accessibilityServiceComponent.configs
          .map { it.notificationTimeout }
          .maxOrNull() ?: 0L

        packageNames = null
      }
    }
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    log { "on accessibility event $event" }
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
    log { "service disconnected" }
    accessibilityComponent?.dispose()
    accessibilityComponent = null
    component.dispose()
    component.accessibilityServiceRef.value = null
    return super.onUnbind(intent)
  }
}

@EntryPoint<ServiceComponent> interface EsAccessibilityServiceComponent {
  val accessibilityEvents: MutableAccessibilityEvents
  val accessibilityComponentFactory: AccessibilityComponentFactory
  val logger: Logger
  val accessibilityServiceRef: MutableStateFlow<EsAccessibilityService?>
}

@EntryPoint<AccessibilityComponent> interface EsAccessibilityServiceAccessibilityComponent {
  val configs: List<AccessibilityConfig> get() = emptyList()
  val scope: ComponentScope<AccessibilityComponent>
}
