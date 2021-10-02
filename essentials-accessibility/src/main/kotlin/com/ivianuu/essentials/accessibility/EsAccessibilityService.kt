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
import com.ivianuu.injekt.android.ServiceScope
import com.ivianuu.injekt.android.createServiceScope
import com.ivianuu.injekt.coroutines.scopedCoroutineScope
import com.ivianuu.injekt.scope.ChildScopeFactory
import com.ivianuu.injekt.scope.ScopeElement
import com.ivianuu.injekt.scope.requireElement
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EsAccessibilityService : AccessibilityService() {
  private val component: EsAccessibilityServiceComponent by lazy {
    requireElement(createServiceScope())
  }

  @Provide private val logger get() = component.logger

  private var accessibilityScope: AccessibilityScope? = null

  override fun onServiceConnected() {
    super.onServiceConnected()
    log { "service connected" }
    accessibilityScope = component.accessibilityScopeFactory()
    component.ref.value = this

    scopedCoroutineScope(scope = accessibilityScope!!).launch(start = CoroutineStart.UNDISPATCHED) {
      val configs = requireElement<EsAccessibilityServiceAccessibilityComponent>(accessibilityScope!!)
        .configs

      log { "update config from $configs" }
      serviceInfo = serviceInfo?.apply {
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
    accessibilityScope?.dispose()
    accessibilityScope = null
    component.serviceScope.dispose()
    component.ref.value = null
    return super.onUnbind(intent)
  }
}

@Provide @ScopeElement<ServiceScope>
class EsAccessibilityServiceComponent(
  val accessibilityEvents: MutableAccessibilityEvents,
  val accessibilityScopeFactory: @ChildScopeFactory () -> AccessibilityScope,
  val logger: Logger,
  val ref: MutableStateFlow<EsAccessibilityService?>,
  val serviceScope: ServiceScope
)

@Provide @ScopeElement<AccessibilityScope>
class EsAccessibilityServiceAccessibilityComponent(
  val configs: Set<AccessibilityConfig> = emptySet(),
)
