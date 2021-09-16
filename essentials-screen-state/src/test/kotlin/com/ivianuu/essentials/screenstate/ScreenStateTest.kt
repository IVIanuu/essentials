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

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class ScreenStateTest {
  @Test fun testScreenState() = runCancellingBlockingTest {
    val broadcasts = EventFlow<Intent>()
    var currentScreenState = ScreenState.OFF
    val globalScopeDispatcher = TestCoroutineDispatcher()
    val globalScope = childCoroutineScope(globalScopeDispatcher)

    val collector = screenState(
      broadcastsFactory = { broadcasts },
      screenStateProvider = { currentScreenState },
      scope = globalScope,
      logger = com.ivianuu.essentials.logging.NoopLogger
    ).testCollect(this)

    globalScopeDispatcher.runCurrent()

    currentScreenState = ScreenState.LOCKED
    broadcasts.emit(Intent())
    currentScreenState = ScreenState.UNLOCKED
    broadcasts.emit(Intent())

    collector.values
      .shouldContainExactly(ScreenState.OFF, ScreenState.LOCKED, ScreenState.UNLOCKED)
  }

  @Test fun testCurrentScreenStateProviderWithScreenOff() = runCancellingBlockingTest {
    val screenState = currentScreenStateProvider(
      TestCoroutineDispatcher(),
      mockk(),
      mockk {
        every { isInteractive } returns false
      }
    )()
    screenState shouldBe ScreenState.OFF
  }

  @Test fun testCurrentScreenStateProviderWithLockedScreen() = runCancellingBlockingTest {
    val screenState = currentScreenStateProvider(
      TestCoroutineDispatcher(),
      mockk {
        every { isDeviceLocked } returns true
      },
      mockk {
        every { isInteractive } returns true
      }
    )()
    screenState shouldBe ScreenState.LOCKED
  }

  @Test fun testCurrentScreenStateProviderWithUnlockedScreen() = runCancellingBlockingTest {
    val screenState = currentScreenStateProvider(
      TestCoroutineDispatcher(),
      mockk {
        every { isDeviceLocked } returns false
      },
      mockk {
        every { isInteractive } returns true
      }
    )()
    screenState shouldBe ScreenState.UNLOCKED
  }
}
