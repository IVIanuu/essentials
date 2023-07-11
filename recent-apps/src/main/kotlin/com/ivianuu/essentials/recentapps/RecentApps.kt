/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.recentapps

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.compose.compositionStateFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter

@JvmInline value class RecentApps(val values: List<String>)

@Provide fun recentApps(
  currentApps: Flow<CurrentApp?>,
  logger: Logger,
  scope: ScopedCoroutineScope<AppScope>
): @Scoped<AppScope> StateFlow<RecentApps> = scope.compositionStateFlow {
  val currentApp = currentApps.collectAsState(null).value?.value
  var recentApps by remember { mutableStateOf(emptyList<String>()) }

  val index = recentApps.indexOf(currentApp)

  // app has not changed
  if (currentApp != null && index != 0) {
    val newRecentApps = recentApps.toMutableList()

    // remove the app from the list
    if (index != -1) {
      newRecentApps.removeAt(index)
    }

    // add the package to the first position
    newRecentApps.add(0, currentApp)

    // make sure that were not getting bigger than the limit
    while (newRecentApps.size > 10) {
      newRecentApps.removeAt(newRecentApps.lastIndex)
    }

    recentApps = newRecentApps
  }

  RecentApps(recentApps)
}

@JvmInline value class CurrentApp(val value: String)

@Provide fun currentApp(
  accessibilityEvents: Flow<AccessibilityEvent>,
  scope: ScopedCoroutineScope<AppScope>
): @Scoped<AppScope> StateFlow<CurrentApp?> = scope.compositionStateFlow {
  produceState<CurrentApp?>(null) {
    accessibilityEvents
      .filter {
        it.type == AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            it.isFullScreen &&
            it.className != "android.inputmethodservice.SoftInputWindow" &&
            it.packageName != null &&
            it.packageName != "android"
      }
      .collect { value = CurrentApp(it.packageName!!) }
  }.value
}

@Provide val currentAppAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )
