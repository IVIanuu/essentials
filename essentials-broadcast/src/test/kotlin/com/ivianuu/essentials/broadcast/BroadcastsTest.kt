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

package com.ivianuu.essentials.broadcast

import android.content.*
import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.test.*
import com.ivianuu.injekt.android.*
import io.kotest.matchers.collections.*
import io.mockk.*
import kotlinx.coroutines.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class BroadcastsTest {
    @Test
    fun testBroadcasts() = runCancellingBlockingTest {
        lateinit var receiver: BroadcastReceiver
        val appContext = mockk<AppContext> {
            every { registerReceiver(any(), any()) } answers {
                receiver = arg(0)
                null
            }
            every { unregisterComponentCallbacks(any()) } returns Unit
        }
        val collector = broadcastsFactory(appContext, coroutineContext[CoroutineDispatcher]!!)
            .invoke("action").testCollect(this)

        receiver.onReceive(appContext, Intent("action"))
        receiver.onReceive(appContext, Intent("action"))

        collector.values.shouldHaveSize(2)
    }
}
