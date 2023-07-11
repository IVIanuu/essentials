/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollectIn
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

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
    ).testCollectIn(this)

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
