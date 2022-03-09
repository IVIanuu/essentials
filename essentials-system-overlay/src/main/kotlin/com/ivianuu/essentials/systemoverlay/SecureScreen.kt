/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.logging.*
import kotlinx.coroutines.flow.*

@JvmInline value class IsOnSecureScreen(val value: Boolean)

@Provide fun isOnSecureScreen(
  accessibilityEvents: Flow<AccessibilityEvent>,
  scope: NamedCoroutineScope<AppScope>,
  L: Logger
): @Eager<AppScope> Flow<IsOnSecureScreen> = accessibilityEvents
  .filter { it.type == AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED }
  .map { it.packageName to it.className }
  .filter { it.second != "android.inputmethodservice.SoftInputWindow" }
  .map { (packageName, className) ->
    var isOnSecureScreen = "packageinstaller" in packageName.orEmpty()
    if (!isOnSecureScreen) {
      isOnSecureScreen = packageName == "com.android.settings" &&
          className == "android.app.MaterialDialog"
    }

    IsOnSecureScreen(isOnSecureScreen)
  }
  .distinctUntilChanged()
  .onEach { log { "on secure screen changed: $it" } }
  .stateIn(scope, SharingStarted.WhileSubscribed(1000), IsOnSecureScreen(false))

@Provide val isOnSecureScreenAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )
