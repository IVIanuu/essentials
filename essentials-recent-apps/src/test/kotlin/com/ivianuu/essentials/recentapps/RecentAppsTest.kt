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

import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.collections.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class RecentAppsTest {
  @Test fun testRecentApps() = runCancellingBlockingTest {
    val recentAppsScopeDispatcher = TestCoroutineDispatcher()
    val recentAppsScope = childCoroutineScope(recentAppsScopeDispatcher)
    val accessibilityEvents = EventFlow<AccessibilityEvent>()
    val collector = recentApps(
      accessibilityEvents,
      com.ivianuu.essentials.logging.NoopLogger, recentAppsScope
    )
      .testCollect(this)

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

    collector.values.shouldContainExactly(
      listOf(),
      listOf("a"),
      listOf("b", "a"),
      listOf("c", "b", "a")
    )
  }

  @Test fun testCurrentApp() = runCancellingBlockingTest {
    val recentApps = EventFlow<List<String>>()
    val collector = currentApp(recentApps).testCollect(this)

    recentApps.emit(listOf("a", "b", "c"))
    recentApps.emit(listOf("a", "b", "c"))
    recentApps.emit(listOf("c", "a", "b"))
    recentApps.emit(listOf("a", "b", "c"))
    recentApps.emit(listOf("b", "c", "a"))

    collector.values.shouldContainExactly("a", "c", "a", "b")
  }
}
