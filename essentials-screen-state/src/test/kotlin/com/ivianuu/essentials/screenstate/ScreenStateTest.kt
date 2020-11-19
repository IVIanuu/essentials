/*
 * Copyright 2020 Manuel Wrage
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

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.test.TestCollector
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import com.ivianuu.essentials.util.NoopLogger
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

    @Test
    fun testScreenState() = runCancellingBlockingTest {
        val broadcasts = EventFlow<Intent>()
        var currentScreenState = ScreenState.Off
        val globalScopeDispatcher = TestCoroutineDispatcher()
        val globalScope = childCoroutineScope(globalScopeDispatcher)

        val collector = screenStateFlow(
            broadcasts = { broadcasts },
            getCurrentScreenState = { currentScreenState },
            globalScope = globalScope,
            logger = NoopLogger
        ).testCollect(this)

        globalScopeDispatcher.runCurrent()

        currentScreenState = ScreenState.Locked
        broadcasts.emit(Intent())
        currentScreenState = ScreenState.Unlocked
        broadcasts.emit(Intent())

        collector.values
            .shouldContainExactly(ScreenState.Off, ScreenState.Locked, ScreenState.Unlocked)
    }

    @Test
    fun testGetCurrentScreenStateWithScreenOff() = runCancellingBlockingTest {
        val screenState = getCurrentScreenState(
            TestCoroutineDispatcher(),
            mockk(),
            mockk {
                every { isInteractive } returns false
            }
        )
        screenState shouldBe ScreenState.Off
    }

    @Test
    fun testGetCurrentScreenStateWithLockedScreen() = runCancellingBlockingTest {
        val screenState = getCurrentScreenState(
            TestCoroutineDispatcher(),
            mockk {
                every { isDeviceLocked } returns true
            },
            mockk {
                every { isInteractive } returns true
            }
        )
        screenState shouldBe ScreenState.Locked
    }

    @Test
    fun testGetCurrentScreenStateWithUnlockedScreen() = runCancellingBlockingTest {
        val screenState = getCurrentScreenState(
            TestCoroutineDispatcher(),
            mockk {
                every { isDeviceLocked } returns false
            },
            mockk {
                every { isInteractive } returns true
            }
        )
        screenState shouldBe ScreenState.Unlocked
    }
}
