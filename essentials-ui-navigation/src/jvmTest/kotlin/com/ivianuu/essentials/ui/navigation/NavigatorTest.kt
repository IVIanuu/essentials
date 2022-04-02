/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

class NavigatorTest {
  object KeyA : Key<Unit>
  object KeyB : Key<Unit>
  object KeyC : Key<Unit>

  @Provide val logger = NoopLogger

  @Test fun testNavigator() = runCancellingBlockingTest {
    val navigator = NavigatorImpl(
      keyHandlers = emptyList(),
      S = this
    )

    val collector = navigator._backStack
      .testCollect(this)

    launch { navigator.push(KeyA) }
    navigator.pop(KeyA)
    launch { navigator.push(KeyB) }
    launch { navigator.replaceTop(KeyC) }
    navigator.popTop()
    launch { navigator.push(KeyB) }
    launch { navigator.setRoot(KeyA) }
    navigator.setBackStack(listOf(KeyC, KeyA, KeyB))
    navigator.clear()

    collector.values.shouldContainExactly(
      listOf(),
      listOf(KeyA),
      listOf(),
      listOf(KeyB),
      listOf(KeyC),
      listOf(),
      listOf(KeyB),
      listOf(KeyA),
      listOf(KeyC, KeyA, KeyB),
      listOf()
    )
  }

  object KeyWithResult : Key<String>

  @Test fun testReturnsResultOnPop() = runCancellingBlockingTest {
    val navigator = NavigatorImpl(
      keyHandlers = emptyList(),
      S = this
    )
    val result = async { navigator.push(KeyWithResult) }
    navigator.pop(KeyWithResult, "b")
    result.await() shouldBe "b"
  }

  @Test fun testReturnsNullResultIfNothingSent() = runCancellingBlockingTest {
    val navigator = NavigatorImpl(
      keyHandlers = emptyList(),
      S = this
    )
    val result = async { navigator.push(KeyWithResult) }
    navigator.popTop()
    result.await() shouldBe null
  }
}
