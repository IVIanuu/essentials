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
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.accessibility.AccessibilityComponent
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.coroutines.StateFlow
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Recent apps provider
 */
@ApplicationScope
@Single
class RecentAppsProvider : AccessibilityComponent() {

    override val config: AccessibilityConfig
        get() = AccessibilityConfig(
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        )

    val currentApp: Flow<String?>
        get() = recentsApps
            .map { it.firstOrNull() }

    private val _recentApps = StateFlow(emptyList<String>())
    val recentsApps: Flow<List<String>>
        get() = _recentApps.distinctUntilChanged()

    private var recentAppsList = mutableListOf<String>()

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

        val recentApps = recentAppsList.toMutableList()
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
        val result = recentApps.chunked(LIMIT).first()
        recentAppsList.clear()
        recentAppsList.addAll(result)

        d { "recent apps changed $result" }

        // push
        _recentApps.value = result
    }

    companion object {
        private const val LIMIT = 10
    }
}
