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
import com.ivianuu.essentials.ui.navigation.NavigationAction.*
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import org.junit.Test

class NavigationStateTest {

    @Test
    fun testNavigationState() = runCancellingBlockingTest {
        val dispatch = EventFlow<NavigationAction>()
        val collector = navigationState(
            intentKeyHandler = { false },
            scope = this,
            actions = dispatch
        ).testCollect(this)

        dispatch.emit(Push("a"))
        dispatch.emit(Pop("a"))
        dispatch.emit(Push("b"))
        dispatch.emit(ReplaceTop("c"))
        dispatch.emit(PopTop("b"))

        collector.values.shouldContainExactly(
            NavigationState(listOf()),
            NavigationState(listOf("a")),
            NavigationState(listOf()),
            NavigationState(listOf("b")),
            NavigationState(listOf("c")),
            NavigationState(listOf())
        )
    }

    @Test
    fun testReturnsResultOnPop() = runCancellingBlockingTest {
        val dispatch = EventFlow<NavigationAction>()
        navigationState(
            intentKeyHandler = { false },
            scope = this,
            actions = dispatch
        ).testCollect(this)

        val result = async {
            pushKeyForResult<String, String>("a", dispatch::emit)
        }
        popTopKeyWithResult("b", dispatch::emit)
        result.await() shouldBe "b"
    }

    @Test
    fun testReturnsNullResultIfNothingSent() = runCancellingBlockingTest {
        val dispatch = EventFlow<NavigationAction>()
        navigationState(
            intentKeyHandler = { false },
            scope = this,
            actions = dispatch
        ).testCollect(this)

        val result = async {
            pushKeyForResult<String, String>("a", dispatch::emit)
        }
        dispatch.emit(PopTop())
        result.await() shouldBe null
    }

}
