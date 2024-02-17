/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.test.*
import io.kotest.matchers.*
import org.junit.*
import kotlin.time.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class RateLimiterTest {
  @Test fun testTryAcquire(): Unit = runCancellingBlockingTest {
    var now = Duration.ZERO
    val rateLimiter = RateLimiter(2, 1.seconds) { now }

    rateLimiter.tryAcquire() shouldBe true
    now += 500.milliseconds
    rateLimiter.tryAcquire() shouldBe true
    rateLimiter.tryAcquire() shouldBe false
    now += 500.milliseconds
    rateLimiter.tryAcquire() shouldBe true
  }

  @Test fun testAcquire(): Unit = runCancellingBlockingTest {
    val rateLimiter = RateLimiter(2, 1.seconds)
    rateLimiter.acquire()
    rateLimiter.acquire()
  }
}
