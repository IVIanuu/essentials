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

package com.ivianuu.essentials.systemoverlay

import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.logging.NoopLogger
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.Test

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
