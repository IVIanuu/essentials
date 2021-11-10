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

package com.ivianuu.essentials.recentapps

import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn

@JvmInline value class RecentApps(val values: List<String>)

@Provide @Scoped<AppComponent>(eager = true) fun recentApps(
  accessibilityEvents: Flow<AccessibilityEvent>,
  scope: ComponentScope<AppComponent>,
  L: Logger
): Flow<RecentApps> = accessibilityEvents
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
  .map { RecentApps(it) }
  .distinctUntilChanged()
  .onEach { log { "recent apps changed $it" } }
  .shareIn(scope, SharingStarted.Eagerly, 1)
  .distinctUntilChanged()

@Provide val recentAppsAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )

@JvmInline value class CurrentApp(val value: String)

@Provide fun currentApp(recentApps: Flow<RecentApps>): Flow<CurrentApp?> =
  recentApps
    .map {
      it.values.firstOrNull()
        ?.let { CurrentApp(it) }
    }
    .distinctUntilChanged()
