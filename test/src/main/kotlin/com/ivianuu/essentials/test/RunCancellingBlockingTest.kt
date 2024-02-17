/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.ivianuu.essentials.test

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlin.coroutines.*

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
  get() = (coroutineContext as CoroutineContext)[CoroutineDispatcher]!! as TestCoroutineDispatcher
