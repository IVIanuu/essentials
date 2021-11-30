/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.recentapps

import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
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
    val collector = recentApps(accessibilityEvents, recentAppsScope, NoopLogger)
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
      RecentApps(listOf()),
      RecentApps(listOf("a")),
      RecentApps(listOf("b", "a")),
      RecentApps(listOf("c", "b", "a"))
    )
  }

  @Test fun testCurrentApp() = runCancellingBlockingTest {
    val recentApps = EventFlow<RecentApps>()
    val collector = currentApp(recentApps).testCollect(this)

    recentApps.emit(RecentApps(listOf("a", "b", "c")))
    recentApps.emit(RecentApps(listOf("a", "b", "c")))
    recentApps.emit(RecentApps(listOf("c", "a", "b")))
    recentApps.emit(RecentApps(listOf("a", "b", "c")))
    recentApps.emit(RecentApps(listOf("b", "c", "a")))

    collector.values.shouldContainExactly(
      CurrentApp("a"),
      CurrentApp("c"),
      CurrentApp("a"),
      CurrentApp("b")
    )
  }
}
