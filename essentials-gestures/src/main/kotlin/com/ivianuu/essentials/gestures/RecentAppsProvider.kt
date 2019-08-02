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
import com.ivianuu.essentials.gestures.accessibility.AccessibilityComponent
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Recent apps provider
 */
@Inject
@ApplicationScope
class RecentAppsProvider : AccessibilityComponent() {

    val currentApp: Flow<String>
        get() = recentsApps
            .map {
                if (it.isNotEmpty()) {
                    it.first()
                } else {
                    PACKAGE_UNKNOWN
                }
            }

    private val _recentApps = ConflatedBroadcastChannel(emptyList<String>())
    val recentsApps: Flow<List<String>>
        get() {
            return _recentApps.openSubscription()
                .consumeAsFlow()
                .distinctUntilChanged()
        }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // were only interested in window state changes
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        // indicates that its a activity
        if (!event.isFullScreen) {
            return
        }

        // nope we don't wanna keyboards in our recent apps list :D
        if (event.className == "android.inputmethodservice.SoftInputWindow") {
            return
        }

        val packageName = event.packageName?.toString()

        if (packageName == null
            || packageName == "android"
        ) {
            return
        }

        handlePackage(packageName)
    }

    private fun handlePackage(packageName: String) {

        val recentApps = getRecentApps().toMutableList()
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

        d { "recent apps changed $result" }

        // push
        _recentApps.offer(result)
    }

    private fun getRecentApps() = _recentApps.value!!

    companion object {
        const val PACKAGE_UNKNOWN = ""

        private const val LIMIT = 10
    }
}