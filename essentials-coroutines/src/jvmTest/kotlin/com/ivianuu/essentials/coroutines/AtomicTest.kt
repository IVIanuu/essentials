/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.test.runCancellingBlockingTest
import io.kotest.matchers.*
import io.kotest.matchers.booleans.*
import org.junit.*

class AtomicTest {
  @Test fun testGet() = runCancellingBlockingTest {
    val atomic = Atomic(1)
    atomic.get() shouldBe 1
    atomic.set(2)
    atomic.get() shouldBe 2
  }

  @Test fun testSet() = runCancellingBlockingTest {
    val atomic = Atomic(1)
    atomic.get() shouldBe 1
    atomic.set(2)
    atomic.get() shouldBe 2
  }

  @Test fun testCompareAndSet() = runCancellingBlockingTest {
    val atomic = Atomic(0)
    atomic.compareAndSet(0, 1).shouldBeTrue()
    atomic.get() shouldBe 1
    atomic.compareAndSet(2, 3).shouldBeFalse()
    atomic.get() shouldBe 1
  }

  @Test fun testGetAndUpdate() = runCancellingBlockingTest {
    val atomic = Atomic(0)
    atomic.getAndUpdate { 1 } shouldBe 0
    atomic.get() shouldBe 1
  }

  @Test fun testUpdate() = runCancellingBlockingTest {
    val atomic = Atomic(0)
    atomic.update { 1 } shouldBe 1
    atomic.get() shouldBe 1
  }
}
