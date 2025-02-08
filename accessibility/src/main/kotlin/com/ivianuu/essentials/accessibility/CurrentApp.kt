/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Stable @Provide @Scoped<AppScope> class CurrentAppProvider(
  accessibilityManager: AccessibilityManager,
  logger: Logger,
  scope: ScopedCoroutineScope<AppScope>
) {
  val currentApp = accessibilityManager.events
    .filter {
      it.type == AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
          it.isFullScreen &&
          it.className != "android.inputmethodservice.SoftInputWindow" &&
          it.packageName != null &&
          it.packageName != "android"
    }
    .map { it.packageName!! }
    .onEach { logger.d { "current app changed $it" } }
    .stateIn(scope, SharingStarted.Eagerly, null)

  @Provide companion object {
    @Provide val accessibilityConfig: AccessibilityConfig
      get() = AccessibilityConfig(
        eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
      )
  }
}
