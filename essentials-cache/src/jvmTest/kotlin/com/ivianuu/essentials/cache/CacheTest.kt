/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.cache

import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.coroutines.par
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.time.seconds
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Test

class CacheTest {
  @Test fun testGetAndPut() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()
    cache.get(0) shouldBe null
    cache.get(1) shouldBe null
    cache.put(0, "a")
    cache.put(1, "b")
    cache.get(0) shouldBe "a"
    cache.get(1) shouldBe "b"
  }

  @Test fun testGetOrLoad() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()
    cache.get(0) { it.toString() } shouldBe "0"
  }

  @Test fun testGetOrLoadErrorWillBePropagatedToTheProducer() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()
    shouldThrow<IllegalStateException> {
      cache.get(0) { throw IllegalStateException() }
    }
  }

  @Test fun testGetOrLoadErrorWillBePropagatedToAInflightConsumer() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()

    launch {
      cache.get(0) {
        delay(100)
        throw IllegalStateException()
      }
    }

    shouldThrow<IllegalStateException> {
      cache.get(0) { "" }
    }
  }

  @Test fun testRemovingInflightValueCancelsInflightComputation() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()

    var cancelledCaller = false
    var cancelledComputation = false
    launch {
      try {
        cache.get(0) {
          onCancel(
            {
              delay(100)
              "0"
            }
          ) {
            cancelledComputation = true
          }
        }
      } catch (e: CancellationException) {
        cancelledCaller = true
      }
    }

    cache.remove(0)

    cancelledComputation shouldBe true
    cancelledCaller shouldBe true
    cache.get(0) shouldBe null
  }

  @Test fun testPutCancelsInflightProducerAndNotifiesConsumer() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()

    val consumerResult = CompletableDeferred<String>()
    var cancelledComputation = false
    launch {
      consumerResult.complete(
        cache.get(0) {
          onCancel(
            {
              delay(100)
              "0"
            }
          ) {
            cancelledComputation = true
          }
        }
      )
    }

    cache.put(0, "1")

    cancelledComputation shouldBe true
    consumerResult.await() shouldBe "1"
    cache.get(0) shouldBe "1"
  }

  @Test fun testAwaitsInflightLoad() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()

    val (a, b) = par(
      {
        cache.get(0) {
          delay(100)
          "0"
        }
      },
      {
        delay(10)
        cache.get(0) { "1" }
      }
    )

    a shouldBe "0"
    a shouldBe b
  }

  @Test fun testGetReturnsNullIfAInflightComputationWasCancelled() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()

    par(
      {
        val inflightJob = launch {
          cache.get(0) {
            delay(1000)
            throw IllegalStateException()
          }
        }
        delay(200)
        inflightJob.cancel()
      },
      {
        delay(100)
        cache.get(0) { "" } shouldBe null
      }
    )
  }

  @Test fun testRemove() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()
    cache.put(0, "a")
    cache.put(1, "b")
    cache.remove(0)
    cache.get(0) shouldBe null
    cache.get(1) shouldBe "b"
  }

  @Test fun testRemoveAll() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()
    cache.put(0, "a")
    cache.put(1, "b")
    cache.removeAll()
    cache.get(0) shouldBe null
    cache.get(1) shouldBe null
  }

  @Test fun testAsMap() = runCancellingBlockingTest {
    val cache = Cache<Int, String>()
    cache.put(0, "a")
    cache.put(1, "b")
    val map = cache.asMap()
    map[0] shouldBe "a"
    map[1] shouldBe "b"
  }

  @Test fun testMaxSize() = runCancellingBlockingTest {
    val cache = Cache<Int, String>(config = CacheConfig(maxSize = 1))
    cache.put(0, "a")
    cache.get(0) shouldBe "a"
    cache.put(1, "b")
    cache.get(1) shouldBe "b"
    cache.get(0) shouldBe null
  }

  @Test fun testExpireAfterWrite() = runCancellingBlockingTest {
    var now = 1.seconds
    val cache = Cache<Int, String>(config = CacheConfig(expireAfterWriteDuration = 1.seconds), clock = { now })
    cache.put(0, "a")
    cache.get(0) shouldBe "a"
    now += 500.milliseconds
    cache.get(0) shouldBe "a"
    now += 500.milliseconds
    cache.get(0) shouldBe null
  }

  @Test fun testExpireAfterAccessWrite() = runCancellingBlockingTest {
    var now = 1.seconds
    val cache = Cache<Int, String>(config = CacheConfig(expireAfterAccessDuration = 1.seconds), clock = { now })
    now += 1.seconds
    cache.get(0) shouldBe null
  }

  @Test fun testExpireAfterAccessRead() = runCancellingBlockingTest {
    var now = 2.seconds
    val cache = Cache<Int, String>(config = CacheConfig(expireAfterAccessDuration = 1.seconds), clock = { now })
    cache.put(0, "a")

    repeat(3) {
      cache.get(0) shouldBe "a"
      now += 500.milliseconds
    }

    now += 1.seconds

    cache.get(0) shouldBe null
  }
}
