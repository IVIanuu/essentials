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

import android.content.ComponentCallbacks
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.injekt.android.ApplicationContext
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
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
    fun testConfigChanges() = runCancellingBlockingTest {
        lateinit var callback: ComponentCallbacks
        val applicationContext = mockk<ApplicationContext> {
            every { registerComponentCallbacks(any()) } answers {
                callback = arg(0)
                Unit
            }
            every { unregisterComponentCallbacks(any()) } returns Unit
        }
        val configChanges = configChanges(applicationContext, Dispatchers.Main)
        var eventCount = 0
        launch { configChanges.collect { eventCount++ } }
        callback.onConfigurationChanged(mockk())
        callback.onConfigurationChanged(mockk())
        callback.onConfigurationChanged(mockk())
        eventCount shouldBe 3
    }
}
