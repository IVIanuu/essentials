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

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.test.runCancellingBlockingTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import org.junit.Test

class LaunchOnTest {
    @Test
    fun testLaunchOnStart() = runCancellingBlockingTest {
        var calls = 0
        flowOf(1, 2)
            .launchOnStart { calls++ }
            .collect()
        calls shouldBe 1
    }

    private data class Event<T>(val value: T, val type: Type) {
        enum class Type {
            START, END
        }
    }

    @Test
    fun testLaunchOnEach() = runCancellingBlockingTest {
        val events = mutableListOf<Event<Int>>()
        val flow = flowOf(1, 2, 3)
            .launchOnEach {
                events += Event(it, Event.Type.START)
                delay(10L * it)
                events += Event(it, Event.Type.END)
            }

        flow.collect()

        events.shouldContainExactly(
            Event(1, Event.Type.START),
            Event(2, Event.Type.START),
            Event(3, Event.Type.START),
            Event(1, Event.Type.END),
            Event(2, Event.Type.END),
            Event(3, Event.Type.END)
        )
    }

    @Test
    fun testLaunchOnEachLatest() = runCancellingBlockingTest {
        val events = mutableListOf<Event<Int>>()
        val flow = flowOf(1, 2, 3)
            .onEach { if (it == 2) delay(20) }
            .launchOnEachLatest {
                events += Event(it, Event.Type.START)
                delay(10L)
                events += Event(it, Event.Type.END)
            }

        flow.collect()

        events.shouldContainExactly(
            Event(1, Event.Type.START),
            Event(1, Event.Type.END),
            Event(2, Event.Type.START),
            Event(3, Event.Type.START),
            Event(3, Event.Type.END)
        )
    }
}