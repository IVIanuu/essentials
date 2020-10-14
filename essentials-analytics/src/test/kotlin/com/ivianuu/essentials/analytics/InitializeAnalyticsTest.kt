package com.ivianuu.essentials.analytics

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.util.BuildInfo
import io.fabric.sdk.android.Fabric
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class InitializeAnalyticsTest {

    @Test
    fun testInitializesInReleaseMode() {
        val called = try {
            initializeAnalytics(
                mockk(),
                com.ivianuu.essentials.util.BuildInfo(
                    isDebug = false,
                    packageName = "",
                    versionCode = 0
                )
            )
            false
        } catch (t: Throwable) {
            true
        }
        expectThat(called).isTrue()
    }

    @Test
    fun testDoesNotInitializeInDebugMode() {
        initializeAnalytics(
            mockk(),
            com.ivianuu.essentials.util.BuildInfo(
                isDebug = true,
                packageName = "",
                versionCode = 0
            )
        )
        expectThat(Fabric.isInitialized()).isFalse()
    }

}