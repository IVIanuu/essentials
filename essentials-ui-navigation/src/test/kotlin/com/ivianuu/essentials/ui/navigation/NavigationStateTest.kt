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
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.navigation.NavigationAction.PopTop
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.NavigationAction.ReplaceTop
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.async
import org.junit.Test

class NavigationStateTest {

    object KeyA : Key<Nothing>
    object KeyB : Key<Nothing>
    object KeyC : Key<Nothing>

    @Test
    fun testNavigationState() = runCancellingBlockingTest {
        val dispatch = EventFlow<NavigationAction>()
        val collector = navigationState(
            intentKeyHandler = { false },
            scope = this,
            actions = dispatch
        ).testCollect(this)

        dispatch.emit(Push(KeyA))
        dispatch.emit(Pop(KeyA))
        dispatch.emit(Push(KeyB))
        dispatch.emit(ReplaceTop(KeyC))
        dispatch.emit(PopTop)

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
        val flow = EventFlow<NavigationAction>()
        val navigator = flow.dispatchAction
        navigationState(
            intentKeyHandler = { false },
            scope = this,
            actions = flow
        ).testCollect(this)

        val result = async {
            navigator.pushForResult(KeyWithResult)
        }
        navigator(Pop(KeyWithResult, "b"))
        result.await() shouldBe "b"
    }

    @Test
    fun testReturnsNullResultIfNothingSent() = runCancellingBlockingTest {
        val flow = EventFlow<NavigationAction>()
        val navigator = flow.dispatchAction
        navigationState(
            intentKeyHandler = { false },
            scope = this,
            actions = flow
        ).testCollect(this)

        val result = async {
            navigator.pushForResult<String>(KeyWithResult)
        }
        flow.emit(PopTop)
        result.await() shouldBe null
    }

}
