/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.recentapps

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@JvmInline value class RecentApps(val values: List<String>)

@Provide fun recentApps(
  currentApps: Flow<CurrentApp?>,
  logger: Logger,
  scope: ScopedCoroutineScope<AppScope>
): @Scoped<AppScope> StateFlow<RecentApps> = scope.compositionStateFlow {
  val currentApp = currentApps.collect(null)?.value
  var recentApps by remember { mutableStateOf(emptyList<String>()) }

  val index = recentApps.indexOf(currentApp)

  // app has not changed
  if (currentApp != null && index != 0) {
    val newRecentApps = recentApps.toMutableList()

    // remove the app from the list
    if (index != -1)
      newRecentApps.removeAt(index)

    // add the package to the first position
    newRecentApps.add(0, currentApp)

    // make sure that were not getting bigger than the limit
    while (newRecentApps.size > 10)
      newRecentApps.removeAt(newRecentApps.lastIndex)

    recentApps = newRecentApps
  }

  RecentApps(recentApps)
}

@JvmInline value class CurrentApp(val value: String)

@Provide fun currentApp(
  accessibilityEvents: Flow<AccessibilityEvent>,
  scope: ScopedCoroutineScope<AppScope>
): @Scoped<AppScope> StateFlow<CurrentApp?> = accessibilityEvents
  .filter {
    it.type == AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
        it.isFullScreen &&
        it.className != "android.inputmethodservice.SoftInputWindow" &&
        it.packageName != null &&
        it.packageName != "android"
  }
  .map { CurrentApp(it.packageName!!) }
  .stateIn(scope, SharingStarted.Lazily, null)

@Provide val currentAppAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )
