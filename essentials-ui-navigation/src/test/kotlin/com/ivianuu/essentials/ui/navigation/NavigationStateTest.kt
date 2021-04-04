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

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.collector
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.navigation.NavigationAction.PopTop
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.NavigationAction.ReplaceTop
import com.ivianuu.essentials.util.NoopLogger
import kotlinx.coroutines.async
import org.junit.Test

class NavigationStateTest {

    object KeyA : Key<Nothing>
    object KeyB : Key<Nothing>
    object KeyC : Key<Nothing>

    @Test
    fun testNavigationState() = runCancellingBlockingTest {
        val actions = EventFlow<NavigationAction>()
        val collector = navigationState(
            intentKeyHandler = { false },
            scope = this,
            actions = actions,
            logger = NoopLogger
        ).testCollect(this)

        actions.emit(Push(KeyA))
        actions.emit(Pop(KeyA))
        actions.emit(Push(KeyB))
        actions.emit(ReplaceTop(KeyC))
        actions.emit(PopTop)

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
        val actions = EventFlow<NavigationAction>()
        val navigator = collector(actions)
        navigationState(
            intentKeyHandler = { false },
            scope = this,
            actions = actions,
            logger = NoopLogger
        ).testCollect(this)

        val result = async {
            navigator.pushForResult(KeyWithResult)
        }
        navigator(Pop(KeyWithResult, "b"))
        result.await() shouldBe "b"
    }

    @Test
    fun testReturnsNullResultIfNothingSent() = runCancellingBlockingTest {
        val actions = EventFlow<NavigationAction>()
        val navigator = collector(actions)
        navigationState(
            intentKeyHandler = { false },
            scope = this,
            actions = actions,
            logger = NoopLogger
        ).testCollect(this)

        val result = async {
            navigator.pushForResult(KeyWithResult)
        }
        actions.emit(PopTop)
        result.await() shouldBe null
    }

}
