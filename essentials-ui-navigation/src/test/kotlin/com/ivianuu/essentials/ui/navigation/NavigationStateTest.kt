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

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.coroutines.stateStore
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.util.NoopLogger
import kotlinx.coroutines.async
import org.junit.Test

class NavigationStateTest {

    object KeyA : Key<Nothing>
    object KeyB : Key<Nothing>
    object KeyC : Key<Nothing>

    @Test
    fun testNavigationState() = runCancellingBlockingTest {
        val navigator = Navigator(
            intentKeyHandler = { false },
            logger = NoopLogger,
            store = stateStore(InternalNavigationState())
        )

        val collector = navigator.testCollect(this)

        navigator.push(KeyA)
        navigator.pop(KeyA)
        navigator.push(KeyB)
        navigator.replaceTop(KeyC)
        navigator.popTop()

        collector.values.shouldContainExactly(
            NavigationState(listOf()),
            NavigationState(listOf(KeyA)),
            NavigationState(listOf()),
            NavigationState(listOf(KeyB)),
            NavigationState(listOf(KeyC)),
            NavigationState(listOf())
        )
    }

    object KeyWithResult : Key<String>

    @Test
    fun testReturnsResultOnPop() = runCancellingBlockingTest {
        val navigator = Navigator(
            intentKeyHandler = { false },
            logger = NoopLogger,
            store = stateStore(InternalNavigationState())
        )
        val result = async { navigator.pushForResult(KeyWithResult) }
        navigator.pop(KeyWithResult, "b")
        result.await() shouldBe "b"
    }

    @Test
    fun testReturnsNullResultIfNothingSent() = runCancellingBlockingTest {
        val navigator = Navigator(
            intentKeyHandler = { false },
            logger = NoopLogger,
            store = stateStore(InternalNavigationState())
        )
        val result = async { navigator.pushForResult(KeyWithResult) }
        navigator.popTop()
        result.await() shouldBe null
    }

}
