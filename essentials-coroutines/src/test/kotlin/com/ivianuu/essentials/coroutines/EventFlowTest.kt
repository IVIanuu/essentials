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
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.delay
import org.junit.Test

class EventFlowTest {

    @Test
    fun testSingleCollector() = runCancellingBlockingTest {
        val eventFlow = EventFlow<Int>()
        val eventCollector = eventFlow.testCollect(this)

        eventFlow.emit(1)
        eventFlow.emit(2)
        eventFlow.emit(3)

        eventCollector.values.shouldContainExactly(1, 2, 3)
    }

    @Test
    fun testMultipleCollectors() = runCancellingBlockingTest {
        val eventFlow = EventFlow<Int>()

        val collector1 = eventFlow.testCollect(this)
        val collector2 = eventFlow.testCollect(this)

        eventFlow.emit(1)
        eventFlow.emit(2)
        eventFlow.emit(3)

        collector1.values.shouldContainExactly(1, 2, 3)
        collector2.values.shouldContainExactly(1, 2, 3)
    }

    @Test
    fun testDoesNotDropEvents() = runCancellingBlockingTest {
        val eventFlow = EventFlow<Int>()
        val collector = eventFlow.testCollect(this) { delay(1000) }

        eventFlow.emit(1)
        eventFlow.emit(2)
        eventFlow.emit(3)

        advanceUntilIdle()

        collector.values.shouldContainExactly(1, 2, 3)
    }

    @Test
    fun testBuffersWhileNoCollectors() = runCancellingBlockingTest {
        val eventFlow = EventFlow<Int>(Int.MAX_VALUE)

        eventFlow.emit(1)
        eventFlow.emit(2)
        eventFlow.emit(3)

        val collector = eventFlow.testCollect(this)

        collector.values.shouldContainExactly(1, 2, 3)
    }

    @Test
    fun testDropsBufferValuesIfExceedsMaxBufferSize() = runCancellingBlockingTest {
        val eventFlow = EventFlow<Int>(2)

        eventFlow.emit(1)
        eventFlow.emit(2)
        eventFlow.emit(3)

        val collector = eventFlow.testCollect(this)

        collector.values.shouldContainExactly(2, 3)
    }
}
