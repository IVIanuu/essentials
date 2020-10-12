package com.ivianuu.essentials.screenstate

import android.content.ComponentCallbacks
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.injekt.android.ApplicationContext
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class ConfigChangesTest {

    @Test
    fun testConfigChanges() = runBlockingTest {
        lateinit var callback: ComponentCallbacks
        val applicationContext = mockk<ApplicationContext> {
            every { registerComponentCallbacks(any()) } answers {
                callback = arg(0)
                Unit
            }
            every { unregisterComponentCallbacks(any()) } returns Unit
        }
        val configChanges = configChanges(applicationContext)
        var eventCount = 0
        val collectorJob = launch { configChanges.collect { eventCount++ } }
        callback.onConfigurationChanged(mockk())
        callback.onConfigurationChanged(mockk())
        callback.onConfigurationChanged(mockk())
        assertEquals(3, eventCount)
        collectorJob.cancelAndJoin()
    }

}
