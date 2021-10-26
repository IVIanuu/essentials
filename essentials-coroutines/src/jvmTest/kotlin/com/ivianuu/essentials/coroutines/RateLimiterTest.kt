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