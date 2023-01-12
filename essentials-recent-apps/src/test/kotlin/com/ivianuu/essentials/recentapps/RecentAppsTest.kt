/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.recentapps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.logging.NoopLogger
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class RecentAppsTest {
  @Test fun testRecentApps() = runCancellingBlockingTest {
    val recentAppsScopeDispatcher = TestCoroutineDispatcher()
    val recentAppsScope = childCoroutineScope(recentAppsScopeDispatcher)
    val accessibilityEvents = EventFlow<AccessibilityEvent>()
    val collector = recentAppsProvider(accessibilityEvents, recentAppsScope, NoopLogger)
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
      RecentAppsProvider(listOf()),
      RecentAppsProvider(listOf("a")),
      RecentAppsProvider(listOf("b", "a")),
      RecentAppsProvider(listOf("c", "b", "a"))
    )
  }

  @Test fun testCurrentApp() = runCancellingBlockingTest {
    val recentApps = EventFlow<RecentAppsProvider>()
    val collector = currentApp(recentApps).testCollect(this)

    recentApps.emit(RecentAppsProvider(listOf("a", "b", "c")))
    recentApps.emit(RecentAppsProvider(listOf("a", "b", "c")))
    recentApps.emit(RecentAppsProvider(listOf("c", "a", "b")))
    recentApps.emit(RecentAppsProvider(listOf("a", "b", "c")))
    recentApps.emit(RecentAppsProvider(listOf("b", "c", "a")))

    collector.values.shouldContainExactly(
      CurrentAppProvider("a"),
      CurrentAppProvider("c"),
      CurrentAppProvider("a"),
      CurrentAppProvider("b")
    )
  }
}
