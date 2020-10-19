/*
 * Copyright 2020 Manuel Wrage
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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.util.NoopLogger
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.containsExactly

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class RecentAppsTest {

    @Test
    fun testRecentApps() = runBlockingTest {
        val recentAppsScopeDispatcher = TestCoroutineDispatcher()
        val recentAppsScope = childCoroutineScope(recentAppsScopeDispatcher)
        val accessibilityEvents = EventFlow<AccessibilityEvent>()
        val recentAppsFlow = recentApps(accessibilityEvents, {
            object : DisposableHandle {
                override fun dispose() {
                }
            }
        }, recentAppsScope, NoopLogger)
        val recentApps = mutableListOf<List<String>>()
        val collectorJob = launch {
            recentAppsFlow.collect {
                recentApps += it
            }
        }
        recentAppsScopeDispatcher.runCurrent()

        accessibilityEvents.emit(
            AccessibilityEvent(
                AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                "a",
                null,
                true
            )
        )

        accessibilityEvents.emit(
            AccessibilityEvent(
                AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                "b",
                null,
                false
            )
        )

        accessibilityEvents.emit(
            AccessibilityEvent(
                AndroidAccessibilityEvent.TYPE_ANNOUNCEMENT,
                "c",
                null,
                true
            )
        )

        accessibilityEvents.emit(
            AccessibilityEvent(
                AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                "b",
                null,
                true
            )
        )

        accessibilityEvents.emit(
            AccessibilityEvent(
                AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                "c",
                null,
                true
            )
        )

        expectThat(recentApps)
            .containsExactly(
                listOf(),
                listOf("a"),
                listOf("b", "a"),
                listOf("c", "b", "a")
            )

        collectorJob.cancelAndJoin()
        recentAppsScope.cancel()
    }

    @Test
    fun testCurrentApp() = runBlockingTest {
        val recentApps = EventFlow<List<String>>()
        val currentApps = mutableListOf<String?>()
        val collectorJob = launch {
            currentApp(recentApps)
                .collect { currentApps += it }
        }

        recentApps.emit(listOf("a", "b", "c"))
        recentApps.emit(listOf("a", "b", "c"))
        recentApps.emit(listOf("c", "a", "b"))
        recentApps.emit(listOf("a", "b", "c"))
        recentApps.emit(listOf("b", "c", "a"))

        expectThat(currentApps)
            .containsExactly(
                "a",
                "c",
                "a",
                "b"
            )

        collectorJob.cancelAndJoin()
    }
}
