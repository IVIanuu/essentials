package com.ivianuu.essentials.screenstate

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.childScope
import com.ivianuu.essentials.util.NoopLogger
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class ScreenStateTest {

    @Test
    fun testScreenState() = runBlockingTest {
        val broadcasts = EventFlow<Intent>()
        var currentScreenState = ScreenState.Off
        val globalScopeDispatcher = TestCoroutineDispatcher()
        val globalScope = childScope(globalScopeDispatcher)
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

        assertEquals(
            listOf(
                ScreenState.Off,
                ScreenState.Locked,
                ScreenState.Unlocked
            ),
            values
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
        assertEquals(ScreenState.Off, screenState)
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
        assertEquals(ScreenState.Locked, screenState)
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
        assertEquals(ScreenState.Unlocked, screenState)
    }

}
