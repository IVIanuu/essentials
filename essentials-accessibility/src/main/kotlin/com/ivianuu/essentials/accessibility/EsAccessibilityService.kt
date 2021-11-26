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
import androidx.compose.runtime.MutableState
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.addFlag
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class EsAccessibilityService : AccessibilityService() {
  private val serviceComponent by lazy {
    application
      .cast<AppElementsOwner>()
      .appElements<EsAccessibilityServiceComponent>()
  }

  @Provide private val logger get() = serviceComponent.logger

  private var accessibilityComponent: AccessibilityComponent? = null

  override fun onServiceConnected() {
    super.onServiceConnected()
    log { "service connected" }
    val accessibilityComponent = serviceComponent.accessibilityComponentFactory(Scope())
      .also { this.accessibilityComponent = it }
    serviceComponent.accessibilityServiceRef.value = this

    accessibilityComponent.coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
      log { "update config from ${accessibilityComponent.configs}" }
      serviceInfo = serviceInfo?.apply {
        eventTypes = accessibilityComponent.configs
          .map { it.eventTypes }
          .fold(0) { acc, events -> acc.addFlag(events) }

        flags = accessibilityComponent.configs
          .map { it.flags }
          .fold(0) { acc, flags -> acc.addFlag(flags) }

        // first one wins
        accessibilityComponent.configs.firstOrNull()?.feedbackType?.let { feedbackType = it }
        feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

        notificationTimeout = accessibilityComponent.configs
          .map { it.notificationTimeout }
          .maxOrNull() ?: 0L

        packageNames = null
      }
    }
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    log { "on accessibility event $event" }
    serviceComponent.accessibilityEvents.tryEmit(
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
    accessibilityComponent?.scope?.dispose()
    accessibilityComponent = null
    serviceComponent.accessibilityServiceRef.value = null
    return super.onUnbind(intent)
  }
}

@Provide @Element<AppScope>
data class EsAccessibilityServiceComponent(
  val accessibilityEvents: MutableSharedFlow<com.ivianuu.essentials.accessibility.AccessibilityEvent>,
  val accessibilityComponentFactory: (Scope<AccessibilityScope>) -> AccessibilityComponent,
  val logger: Logger,
  val accessibilityServiceRef: MutableState<EsAccessibilityService?>
)

@Provide data class AccessibilityComponent(
  val configs: List<AccessibilityConfig>,
  val coroutineScope: NamedCoroutineScope<AccessibilityScope>,
  val elements: Elements<AccessibilityScope>,
  val scope: Scope<AccessibilityScope>
)
