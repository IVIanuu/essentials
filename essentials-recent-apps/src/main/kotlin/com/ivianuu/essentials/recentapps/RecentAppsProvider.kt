/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.recentapps

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.coroutines.share
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan

@JvmInline value class RecentAppsProvider(val recentApps: Flow<List<String>>)

context(AccessibilityEvent.Provider, Logger, NamedCoroutineScope<AppScope>)
    @Provide fun recentAppsProvider(): @Scoped<AppScope> RecentAppsProvider = RecentAppsProvider(
  accessibilityEvents
    .filter { it.type == AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED }
    .filter { it.isFullScreen }
    .filter { it.className != "android.inputmethodservice.SoftInputWindow" }
    .mapNotNull { it.packageName }
    .filter { it != "android" }
    .scan(emptyList<String>()) { recentApps, currentApp ->
      val index = recentApps.indexOf(currentApp)

      // app has not changed
      if (index == 0) return@scan recentApps

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

      newRecentApps
    }
    .distinctUntilChanged()
    .onEach { log { "recent apps changed $it" } }
    .share(SharingStarted.Eagerly, 1)
    .distinctUntilChanged()
)

@Provide val recentAppsAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )