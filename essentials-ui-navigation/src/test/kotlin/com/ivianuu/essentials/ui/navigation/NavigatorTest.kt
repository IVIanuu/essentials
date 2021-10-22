/*
 * Copyright 2021 Manuel Wrage
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

import com.ivianuu.essentials.logging.NoopLogger
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.junit.Test

class NavigatorTest {
  object KeyA : Key<Unit>
  object KeyB : Key<Unit>
  object KeyC : Key<Unit>

  @Test fun testNavigator() = runCancellingBlockingTest {
    val navigator = NavigatorImpl(
      keyHandlers = emptyList(),
      logger = NoopLogger,
      scope = this
    )

    val collector = navigator.backStack.testCollect(this)

    launch { navigator.push(KeyA) }
    navigator.pop(KeyA)
    launch { navigator.push(KeyB) }
    launch { navigator.replaceTop(KeyC) }
    navigator.popTop()
    launch { navigator.push(KeyB) }
    launch { navigator.setRoot(KeyA) }

    collector.values.shouldContainExactly(
      listOf(),
      listOf(KeyA),
      listOf(),
      listOf(KeyB),
      listOf(KeyC),
      emptyList(),
      listOf(KeyB),
      listOf(KeyA)
    )
  }

  object KeyWithResult : Key<String>

  @Test fun testReturnsResultOnPop() = runCancellingBlockingTest {
    val navigator = NavigatorImpl(
      keyHandlers = emptyList(),
      logger = NoopLogger,
      scope = this
    )
    val result = async { navigator.push(KeyWithResult) }
    navigator.pop(KeyWithResult, "b")
    result.await() shouldBe "b"
  }

  @Test fun testReturnsNullResultIfNothingSent() = runCancellingBlockingTest {
    val navigator = NavigatorImpl(
      keyHandlers = emptyList(),
      logger = NoopLogger,
      scope = this
    )
    val result = async { navigator.push(KeyWithResult) }
    navigator.popTop()
    result.await() shouldBe null
  }
}
