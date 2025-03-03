/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.navigation

import essentials.test.*
import injekt.*
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import kotlinx.coroutines.*
import org.junit.*

class NavigatorTest {
  object ScreenA : Screen<Unit>
  object ScreenB : Screen<Unit>
  object ScreenC : Screen<Unit>

  @Provide val logger = NoopLogger

  @Test fun testNavigator() = runCancellingBlockingTest {
    val navigator = Navigator()

    val collector = navigator.backStack
      .testCollectIn(this)

    launch { navigator.push(ScreenA) }
    navigator.pop(ScreenA)
    launch { navigator.push(ScreenB) }
    launch { navigator.replaceTop(ScreenC) }
    navigator.popTop()
    launch { navigator.push(ScreenB) }
    launch { navigator.setRoot(ScreenA) }
    navigator.setBackStack(listOf(ScreenC, ScreenA, ScreenB))
    navigator.clear()

    collector.values.shouldContainExactly(
      listOf(),
      listOf(ScreenA),
      listOf(),
      listOf(ScreenB),
      listOf(ScreenC),
      listOf(),
      listOf(ScreenB),
      listOf(ScreenA),
      listOf(ScreenC, ScreenA, ScreenB),
      listOf()
    )
  }

  object ScreenWithResult : Screen<String>

  @Test fun testReturnsResultOnPop() = runCancellingBlockingTest {
    val navigator = Navigator()
    val result = async { navigator.push(ScreenWithResult) }
    navigator.pop(ScreenWithResult, "b")
    result.await() shouldBe "b"
  }

  @Test fun testReturnsNullResultIfNothingSent() = runCancellingBlockingTest {
    val navigator = Navigator()
    val result = async { navigator.push(ScreenWithResult) }
    navigator.popTop()
    result.await() shouldBe null
  }
}
