/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.content.*
import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.mockk.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

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
      screenStateProvider = { currentScreenState }
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
