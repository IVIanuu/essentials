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

package com.ivianuu.essentials.screenstate

import android.util.DisplayMetrics
import android.view.WindowManager
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class DisplayInfoTest {
  @Test fun testDisplayInfo() = runCancellingBlockingTest {
    val configChanges = EventFlow<ConfigChange>()
    val rotation = MutableStateFlow(DisplayRotation.PORTRAIT_UP)
    var currentWidth = 0
    var currentHeight = 0
    val windowManager = mockk<WindowManager> {
      every { defaultDisplay } returns mockk {
        every { getRealMetrics(any()) } answers {
          val metrics = arg<DisplayMetrics>(0)
          metrics.widthPixels = currentWidth
          metrics.heightPixels = currentHeight
        }
      }
    }

    val collector = displayInfo(
      { configChanges },
      { rotation },
      windowManager
    ).testCollect(this)
    advanceUntilIdle()

    collector.values.shouldHaveSize(1)
    collector.values[0] shouldBe DisplayInfo(DisplayRotation.PORTRAIT_UP, 0, 0)

    // config change
    currentWidth = 101
    currentHeight = 102
    configChanges.tryEmit(ConfigChange)
    collector.values.shouldHaveSize(2)
    collector.values[1] shouldBe DisplayInfo(DisplayRotation.PORTRAIT_UP, 101, 102)

    // rotation change
    rotation.value = DisplayRotation.LANDSCAPE_LEFT
    collector.values.shouldHaveSize(3)
    collector.values[2] shouldBe DisplayInfo(DisplayRotation.LANDSCAPE_LEFT, 101, 102)

    // unrelated change
    configChanges.tryEmit(ConfigChange)
    collector.values.shouldHaveSize(3)
  }
}
