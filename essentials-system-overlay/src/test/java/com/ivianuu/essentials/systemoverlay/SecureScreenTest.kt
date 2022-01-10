/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.collections.*
import org.junit.*

class SecureScreenTest {
  @Test fun testIsOnSecureScreen() = runCancellingBlockingTest {
    val accessibilityEvents = EventFlow<AccessibilityEvent>()
    val collector = isOnSecureScreen(accessibilityEvents, this, NoopLogger)
      .testCollect(this)

    val idleEvent = AccessibilityEvent(
      AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
      null,
      null,
      false
    )

    accessibilityEvents.emit(
      AccessibilityEvent(
        AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
        "com.android.settings",
        "android.app.MaterialDialog",
        true
      )
    )

    accessibilityEvents.emit(idleEvent)

    accessibilityEvents.emit(
      AccessibilityEvent(
        AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
        "packageinstaller",
        "packageinstaller",
        true
      )
    )

    accessibilityEvents.emit(idleEvent)

    accessibilityEvents.emit(
      AccessibilityEvent(
        AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
        "android",
        "android.inputmethodservice.SoftInputWindow",
        true
      )
    )

    collector.values.shouldContainExactly(
      IsOnSecureScreen(false),
      IsOnSecureScreen(true),
      IsOnSecureScreen(false),
      IsOnSecureScreen(true),
      IsOnSecureScreen(false)
    )
  }
}
