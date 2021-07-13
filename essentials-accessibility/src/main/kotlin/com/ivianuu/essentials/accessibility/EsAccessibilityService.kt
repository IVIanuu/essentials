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

import android.accessibilityservice.*
import android.content.*
import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*

class EsAccessibilityService : AccessibilityService() {
  private val component: EsAccessibilityServiceComponent by lazy {
    createServiceScope()
      .element()
  }

  @Provide private val logger get() = component.logger

  private var accessibilityScope: AccessibilityScope? = null

  override fun onServiceConnected() {
    super.onServiceConnected()
    d { "service connected" }
    accessibilityScope = component.accessibilityScopeFactory()
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    d { "on accessibility event $event" }
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
    d { "service disconnected" }
    accessibilityScope?.cast<DisposableScope>()?.dispose()
    accessibilityScope = null
    component.serviceScope.cast<DisposableScope>().dispose()
    return super.onUnbind(intent)
  }
}

@Provide @ScopeElement<ServiceScope>
class EsAccessibilityServiceComponent(
  val accessibilityEvents: MutableAccessibilityEvents,
  val accessibilityScopeFactory: @ChildScopeFactory () -> AccessibilityScope,
  val logger: Logger,
  val serviceScope: ServiceScope
)
