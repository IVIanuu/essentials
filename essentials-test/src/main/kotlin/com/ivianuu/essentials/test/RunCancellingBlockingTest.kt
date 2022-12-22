/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ivianuu.essentials.test

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UncompletedCoroutinesError
import kotlinx.coroutines.test.runBlockingTest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun runCancellingBlockingTest(
  context: CoroutineContext = EmptyCoroutineContext,
  testBody: suspend context(TestCoroutineScope) () -> Unit
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

context(TestCoroutineScope) fun runCancellingBlockingTest(
  testBody: suspend context(TestCoroutineScope) () -> Unit
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

@OptIn(ExperimentalStdlibApi::class) val TestCoroutineScope.dispatcher: TestCoroutineDispatcher
  get() = (coroutineContext as CoroutineContext)[CoroutineDispatcher]!! as TestCoroutineDispatcher
