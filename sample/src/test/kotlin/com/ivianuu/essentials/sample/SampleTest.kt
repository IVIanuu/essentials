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
import com.ivianuu.essentials.sample.ui.CounterAction.*
import com.ivianuu.essentials.sample.ui.CounterState
import com.ivianuu.essentials.sample.ui.CounterStore
import com.ivianuu.essentials.test.TestCollector
import com.ivianuu.essentials.test.collectIn
import com.ivianuu.essentials.test.runCancellingBlockingTest
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.Test

class SampleTest {

    @Test
    fun test() {
    }

    @Test
    fun testCounter() = runCancellingBlockingTest {
        val actions = EventFlow<CounterAction>()
        val collector = TestCollector<CounterState>()
        CounterStore(scope = this, actions = actions)
            .collectIn(this, collector)

        actions.emit(Inc)
        actions.emit(Dec)
        collector.values.shouldContainExactly(
            CounterState(0),
            CounterState(1),
            CounterState(0)
        )
    }

}
