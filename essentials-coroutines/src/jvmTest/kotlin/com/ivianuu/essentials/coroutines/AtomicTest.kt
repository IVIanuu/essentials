package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.test.runCancellingBlockingTest
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.Test

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
