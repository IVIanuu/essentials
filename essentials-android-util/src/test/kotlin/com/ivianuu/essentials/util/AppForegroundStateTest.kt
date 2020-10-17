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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import strikt.api.expectThat
import strikt.assertions.containsExactly

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class AppForegroundStateTest {

    @Test
    fun testAppForegroundState() = runBlockingTest {
        val lifecycleOwner = object : LifecycleOwner {
            private val _lifecycle = LifecycleRegistry(this)
            override fun getLifecycle(): Lifecycle = _lifecycle
        }
        val lifecycleRegistry = lifecycleOwner.lifecycle as LifecycleRegistry

        val events = mutableListOf<Boolean>()
        val collectorJob = launch {
            appForegroundState(Dispatchers.Main, lifecycleOwner)
                .collect { events += it }
        }

        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED

        expectThat(events).containsExactly(
            false,
            true,
            false,
            true,
            false
        )

        collectorJob.cancelAndJoin()
    }
}
