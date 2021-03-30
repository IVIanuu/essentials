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
import com.ivianuu.essentials.test.testCollect
import com.ivianuu.injekt.android.AppContext
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class ConfigChangesTest {
    @Test
    fun testConfigChanges() = runCancellingBlockingTest {
        lateinit var callback: ComponentCallbacks
        val appContext = mockk<AppContext> {
            every { registerComponentCallbacks(any()) } answers {
                callback = arg(0)
                Unit
            }
            every { unregisterComponentCallbacks(any()) } returns Unit
        }
        val collector = configChanges(appContext, coroutineContext[CoroutineDispatcher]!!)
            .testCollect(this)

        callback.onConfigurationChanged(mockk())
        callback.onConfigurationChanged(mockk())
        callback.onConfigurationChanged(mockk())

        collector.values.shouldHaveSize(3)
    }
}
