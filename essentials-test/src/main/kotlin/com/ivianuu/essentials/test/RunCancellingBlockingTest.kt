/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.test

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UncompletedCoroutinesError
import kotlinx.coroutines.test.runBlockingTest

fun runCancellingBlockingTest(
  context: CoroutineContext = EmptyCoroutineContext,
  testBody: suspend TestCoroutineScope.() -> Unit
) {
  var testsPassed = false
  try {
    runBlockingTest(context) {
      testBody(this)
      testsPassed = true
      cancel()
    }
  } catch (e: UncompletedCoroutinesError) {
    if (testsPassed) {
      // we are okay - this is a workaround for https://github.com/Kotlin/kotlinx.coroutines/issues/1531
    } else {
      throw e
    }
  } catch (e: CancellationException) {
    // ok job got cancelled
  }
}

fun TestCoroutineScope.runCancellingBlockingTest(
  testBody: suspend TestCoroutineScope.() -> Unit
) {
  var testsPassed = false
  try {
    runBlockingTest {
      testBody(this)
      testsPassed = true
      cancel()
    }
  } catch (e: UncompletedCoroutinesError) {
    if (testsPassed) {
      // we are okay - this is a workaround for https://github.com/Kotlin/kotlinx.coroutines/issues/1531
    } else {
      throw e
    }
  } catch (e: CancellationException) {
    // ok job got cancelled
  }
}

val TestCoroutineScope.dispatcher: TestCoroutineDispatcher
  get() = coroutineContext[CoroutineDispatcher]!! as TestCoroutineDispatcher
