/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.Test

class KeyboardVisibilityTest {
  @Test fun testKeyboardVisibility() = runCancellingBlockingTest {
    val accessibilityEvents = EventFlow<AccessibilityEvent>()
    var keyboardHeight = 0
    val collector = keyboardVisible(
      accessibilityEvents,
      { keyboardHeight },
      this
    ).testCollect(this)

    keyboardHeight = 1
    accessibilityEvents.emit(
      AccessibilityEvent(
        type = 0,
        isFullScreen = true,
        packageName = null,
        className = "android.inputmethodservice.SoftInputWindow"
      )
    )
    keyboardHeight = 0
    advanceTimeBy(100)

    collector.values.shouldContainExactly(
      KeyboardVisible(false),
      KeyboardVisible(true),
      KeyboardVisible(false)
    )
  }
}
