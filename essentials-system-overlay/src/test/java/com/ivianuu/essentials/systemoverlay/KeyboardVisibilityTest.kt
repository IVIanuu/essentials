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

import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.collections.*
import org.junit.*

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
      false,
      true,
      false
    )
  }
}
