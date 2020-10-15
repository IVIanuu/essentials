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
        expectThat(called).isTrue()
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
        expectThat(Fabric.isInitialized()).isFalse()
    }
}
