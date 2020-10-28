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

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class EventFlowTest {

    @Test
    fun testSingleCollector() = runBlockingTest {
        val eventFlow = EventFlow<Int>()
        val values = mutableListOf<Int>()
        val collectorJob = launch {
            eventFlow.collect { values += it }
        }

        eventFlow.emit(1)
        eventFlow.emit(2)
        eventFlow.emit(3)

        values.shouldContainExactly(1, 2, 3)

        collectorJob.cancelAndJoin()
    }

    @Test
    fun testMultipleCollectors() = runBlockingTest {
        val eventFlow = EventFlow<Int>()

        val values1 = mutableListOf<Int>()
        val collectorJob1 = launch {
            eventFlow.collect { values1 += it }
        }
        val values2 = mutableListOf<Int>()
        val collectorJob2 = launch {
            eventFlow.collect { values2 += it }
        }

        eventFlow.emit(1)
        eventFlow.emit(2)
        eventFlow.emit(3)

        values1.shouldContainExactly(1, 2, 3)
        values2.shouldContainExactly(1, 2, 3)

        collectorJob1.cancelAndJoin()
        collectorJob2.cancelAndJoin()
    }

    @Test
    fun testDoesNotDropEvents() = runBlockingTest {
        val eventFlow = EventFlow<Int>()
        val values = mutableListOf<Int>()
        val collectorJob = launch {
            eventFlow.collect {
                values += it
                delay(1000)
            }
        }

        eventFlow.emit(1)
        eventFlow.emit(2)
        eventFlow.emit(3)

        advanceUntilIdle()

        values.shouldContainExactly(1, 2, 3)

        collectorJob.cancelAndJoin()
    }

    @Test
    fun testBuffersWhileNoCollectors() = runBlockingTest {
        val eventFlow = EventFlow<Int>(Int.MAX_VALUE)

        eventFlow.emit(1)
        eventFlow.emit(2)
        eventFlow.emit(3)

        val values = mutableListOf<Int>()
        val collectorJob = launch {
            eventFlow.collect {
                values += it
            }
        }

        values.shouldContainExactly(1, 2, 3)

        collectorJob.cancelAndJoin()
    }

    @Test
    fun testDropsBufferValuesIfExceedsMaxBufferSize() = runBlockingTest {
        val eventFlow = EventFlow<Int>(2)

        eventFlow.emit(1)
        eventFlow.emit(2)
        eventFlow.emit(3)

        val values = mutableListOf<Int>()
        val collectorJob = launch {
            eventFlow.collect {
                values += it
            }
        }

        values.shouldContainExactly(2, 3)

        collectorJob.cancelAndJoin()
    }
}
