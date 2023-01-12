/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.coroutines.state
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@JvmInline value class IsOnSecureScreen(val value: Boolean)

context(Logger, NamedCoroutineScope<AppScope>) @Provide fun isOnSecureScreen(
  accessibilityEvents: Flow<AccessibilityEvent>
): @Scoped<AppScope> Flow<IsOnSecureScreen> = accessibilityEvents
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
  .state(SharingStarted.WhileSubscribed(1000), IsOnSecureScreen(false))

@Provide val isOnSecureScreenAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )
