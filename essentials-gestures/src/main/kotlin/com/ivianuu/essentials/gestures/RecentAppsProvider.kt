/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.gestures

import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AccessibilityComponent
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.BindAccessibilityComponent
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ApplicationScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/**
 * Recent apps provider
 */
@BindAccessibilityComponent
@ApplicationScoped
class RecentAppsProvider(
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger
) : AccessibilityComponent() {

    override val config: AccessibilityConfig
        get() = AccessibilityConfig(
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        )

    val currentApp: Flow<String?>
        get() = recentsApps
            .map { it.firstOrNull() }

    private val _recentApps = MutableStateFlow(emptyList<String>())
    val recentsApps: StateFlow<List<String>> get() = _recentApps

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // indicates that its a activity
        if (!event.isFullScreen) {
            return
        }

        // nope we don't wanna keyboards in our recent apps list :D
        if (event.className == "android.inputmethodservice.SoftInputWindow") {
            return
        }

        val packageName = event.packageName?.toString()

        if (packageName == null ||
            packageName == "android"
        ) {
            return
        }

        handlePackage(packageName)
    }

    private fun handlePackage(packageName: String) {
        val recentApps = _recentApps.value.toMutableList()
        val index = recentApps.indexOf(packageName)

        // app has not changed
        if (index == 0) {
            return
        }

        // remove the app from the list
        if (index != -1) {
            recentApps.removeAt(index)
        }

        // add the package to the first position
        recentApps.add(0, packageName)

        // make sure that were not getting bigger than the limit
        val finalRecentApps = recentApps.chunked(10).first()

        logger.d("recent apps changed $finalRecentApps")

        // push
        _recentApps.value = finalRecentApps
    }
}
