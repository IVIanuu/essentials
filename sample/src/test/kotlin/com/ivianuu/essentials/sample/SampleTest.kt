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

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.sample.ui.CounterAction
import com.ivianuu.essentials.sample.ui.CounterAction.Dec
import com.ivianuu.essentials.sample.ui.CounterAction.Inc
import com.ivianuu.essentials.sample.ui.CounterState
import com.ivianuu.essentials.sample.ui.counterState
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import com.ivianuu.essentials.util.Toaster
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.Test

class SampleTest {

    @Test
    fun testCounter() = runCancellingBlockingTest {
        val actions = EventFlow<CounterAction>()
        val collector = counterState(scope = this, actions = actions, toaster = mockk())
            .testCollect(this)

        actions.emit(Inc)
        actions.emit(Dec)

        collector.values.shouldContainExactly(
            CounterState(0),
            CounterState(1),
            CounterState(0)
        )
    }

    @Test
    fun testCannotDecrementZero() = runCancellingBlockingTest {
        val actions = EventFlow<CounterAction>()
        var toastCalled = false

        counterState(scope = this, actions = actions, toaster = object : Toaster {
            override fun showToast(message: String) {
                toastCalled = true
            }

            override fun showToast(messageRes: Int, vararg arguments: Any?) {
                toastCalled = true
            }
        }).testCollect(this)

        actions.emit(Dec)

        toastCalled shouldBe true
    }

}
