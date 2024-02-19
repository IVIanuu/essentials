/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide @Scoped<AppScope> class CurrentAppProvider(
  accessibilityEvents: Flow<AccessibilityEvent>,
  scope: ScopedCoroutineScope<AppScope>
) {
  val currentApp = accessibilityEvents
    .filter {
      it.type == AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
          it.isFullScreen &&
          it.className != "android.inputmethodservice.SoftInputWindow" &&
          it.packageName != null &&
          it.packageName != "android"
    }
    .map { it.packageName!! }
    .stateIn(scope, SharingStarted.Lazily, null)

  companion object {
    @Provide val currentAppAccessibilityConfig: AccessibilityConfig
      get() = AccessibilityConfig(
        eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
      )
  }
}
