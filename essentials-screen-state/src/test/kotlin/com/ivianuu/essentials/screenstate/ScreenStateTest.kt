package com.ivianuu.essentials.screenstate

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.util.NoopLogger
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class ScreenStateTest {

    @Test
    fun testScreenState() = runBlockingTest {
        val broadcasts = EventFlow<Intent>()
        var currentScreenState = ScreenState.Off
        val globalScopeDispatcher = TestCoroutineDispatcher()
        val globalScope = childCoroutineScope(globalScopeDispatcher)
        val screenStateFlow = screenStateFlow(
            broadcasts = { broadcasts },
            getCurrentScreenState = { currentScreenState },
            globalScope = globalScope,
            logger = NoopLogger
        )

        val values = mutableListOf<ScreenState>()
        val collectorJob = launch {
            screenStateFlow.collect { values += it }
        }

        globalScopeDispatcher.runCurrent()

        currentScreenState = ScreenState.Locked
        broadcasts.offer(Intent())
        currentScreenState = ScreenState.Unlocked
        broadcasts.offer(Intent())

        expectThat(values)
            .containsExactly(
                ScreenState.Off,
                ScreenState.Locked,
                ScreenState.Unlocked
            )

        collectorJob.cancelAndJoin()
        globalScope.cancel()
    }

    @Test
    fun testGetCurrentScreenStateWithScreenOff() = runBlockingTest {
        val screenState = getCurrentScreenState(
            TestCoroutineDispatcher(),
            mockk(),
            mockk {
                every { isInteractive } returns false
            }
        )
        expectThat(screenState).isEqualTo(ScreenState.Off)
    }

    @Test
    fun testGetCurrentScreenStateWithLockedScreen() = runBlockingTest {
        val screenState = getCurrentScreenState(
            TestCoroutineDispatcher(),
            mockk {
                every { isDeviceLocked } returns true
            },
            mockk {
                every { isInteractive } returns true
            }
        )
        expectThat(screenState).isEqualTo(ScreenState.Locked)
    }

    @Test
    fun testGetCurrentScreenStateWithUnlockedScreen() = runBlockingTest {
        val screenState = getCurrentScreenState(
            TestCoroutineDispatcher(),
            mockk {
                every { isDeviceLocked } returns false
            },
            mockk {
                every { isInteractive } returns true
            }
        )
        expectThat(screenState).isEqualTo(ScreenState.Unlocked)
    }

}
