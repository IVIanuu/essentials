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

import android.content.BroadcastReceiver
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.test.TestCollector
import com.ivianuu.essentials.test.collectIn
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.injekt.android.ApplicationContext
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class BroadcastsTest {

    @Test
    fun testBroadcasts() = runCancellingBlockingTest {
        lateinit var receiver: BroadcastReceiver
        val applicationContext = mockk<ApplicationContext> {
            every { registerReceiver(any(), any()) } answers {
                receiver = arg(0)
                null
            }
            every { unregisterComponentCallbacks(any()) } returns Unit
        }
        val broadcasts = broadcasts(applicationContext, Dispatchers.Main, "action")
        val collector = TestCollector<Intent>()
        broadcasts.collectIn(this, collector)

        receiver.onReceive(applicationContext, Intent("action"))
        receiver.onReceive(applicationContext, Intent("action"))

        collector.values.shouldHaveSize(2)
    }
}
