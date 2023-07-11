/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.time.seconds
import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.time.Duration

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
