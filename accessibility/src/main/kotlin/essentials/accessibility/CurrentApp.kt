/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.accessibility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import essentials.*
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.flow.*

@Tag typealias CurrentApp = String

@Provide @Composable fun currentApp(
  accessibilityEvents: Flow<AccessibilityEvent>,
  logger: Logger
): @ComposeIn<AppScope> CurrentApp? = produceState(nullOf()) {
  accessibilityEvents
    .filter {
      it.type == AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
          it.isFullScreen &&
          it.className != "android.inputmethodservice.SoftInputWindow" &&
          it.packageName != null &&
          it.packageName != "android"
    }
    .map { it.packageName!! }
    .onEach { logger.d { "current app changed $it" } }
    .collect { value = it }
}.value

@Provide val currentAppAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )
