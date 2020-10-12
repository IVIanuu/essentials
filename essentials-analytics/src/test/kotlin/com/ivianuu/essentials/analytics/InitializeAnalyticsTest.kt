package com.ivianuu.essentials.analytics

import com.ivianuu.essentials.util.BuildInfo
import io.fabric.sdk.android.Fabric
import io.mockk.mockk
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InitializeAnalyticsTest {

    @Test
    fun testInitializesInReleaseMode() {
        val called = try {
            initializeAnalytics(
                mockk(),
                BuildInfo(
                    isDebug = false,
                    packageName = "",
                    versionCode = 0
                )
            )
            false
        } catch (t: Throwable) {
            true
        }
        assertTrue(called)
    }

    @Test
    fun testDoesNotInitializeInDebugMode() {
        initializeAnalytics(
            mockk(),
            BuildInfo(
                isDebug = true,
                packageName = "",
                versionCode = 0
            )
        )
        assertFalse(Fabric.isInitialized())
    }

}