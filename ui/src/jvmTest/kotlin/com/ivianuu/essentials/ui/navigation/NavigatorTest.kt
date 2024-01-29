/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.logging.NoopLogger
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollectIn
import com.ivianuu.injekt.Provide
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.junit.Test

class NavigatorTest {
  class ScreenA : Screen<Unit>
  class ScreenB : Screen<Unit>
  class ScreenC : Screen<Unit>

  @Provide val logger = NoopLogger

  @Test fun testNavigator() = runCancellingBlockingTest {
    val navigator = NavigatorImpl(
      screenInterceptors = emptyList(),
      scope = this
    )

    val collector = navigator._backStack
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
    val navigator = NavigatorImpl(
      screenInterceptors = emptyList(),
      scope = this
    )
    val result = async { navigator.push(ScreenWithResult) }
    navigator.pop(ScreenWithResult, "b")
    result.await() shouldBe "b"
  }

  @Test fun testReturnsNullResultIfNothingSent() = runCancellingBlockingTest {
    val navigator = NavigatorImpl(
      screenInterceptors = emptyList(),
      scope = this
    )
    val result = async { navigator.push(ScreenWithResult) }
    navigator.popTop()
    result.await() shouldBe null
  }
}
