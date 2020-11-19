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

package com.ivianuu.essentials.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.test.TestCollector
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.Dispatchers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class AppForegroundStateTest {

    @Test
    fun testAppForegroundState() = runCancellingBlockingTest {
        val lifecycleOwner = object : LifecycleOwner {
            private val _lifecycle = LifecycleRegistry(this)
            override fun getLifecycle(): Lifecycle = _lifecycle
        }
        val lifecycleRegistry = lifecycleOwner.lifecycle as LifecycleRegistry
        val collector = appForegroundState(Dispatchers.Main, lifecycleOwner)
            .testCollect(this)

        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED

        collector.values.shouldContainExactly(
            false,
            true,
            false,
            true,
            false
        )
    }
}
