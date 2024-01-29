/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.test.runCancellingBlockingTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class SharedTest {
  @Test fun testSharedComputation() = runCancellingBlockingTest {
    val calls = mutableMapOf<Int, Int>()
    val shared = sharedComputation<Int, Unit> { key ->
      delay(10.milliseconds)
      calls[key] = calls[key]?.inc() ?: 1
    }

    par(
      {
        shared(0)
      },
      {
        delay(5.milliseconds)
        shared(0)
      },
      {
        shared(1)
      }
    )

    calls[0] shouldBe 1
    calls[1] shouldBe 1
  }

  @Test fun testSharedResource() = runCancellingBlockingTest {
    val createCalls = mutableMapOf<Int, Int>()
    val releaseCalls = mutableMapOf<Int, Int>()
    val shared = sharedResource<Int, Unit>(
      create = { createCalls[it] = createCalls[it]?.inc() ?: 1 },
      release = { key, _ -> releaseCalls[key] = releaseCalls[key]?.inc() ?: 1 }
    )

    val (_, release0A) = shared(0)
    val (_, release0B) = shared(0)

    createCalls[0] shouldBe 1
    releaseCalls[0] shouldBe null
    createCalls[1] shouldBe null
    releaseCalls[1] shouldBe null

    release0A()

    createCalls[0] shouldBe 1
    releaseCalls[0] shouldBe null
    createCalls[1] shouldBe null
    releaseCalls[1] shouldBe null

    release0B()
    val (_, release1A) = shared(1)

    createCalls[0] shouldBe 1
    releaseCalls[0] shouldBe 1
    createCalls[1] shouldBe 1
    releaseCalls[1] shouldBe null

    release1A()

    createCalls[0] shouldBe 1
    releaseCalls[0] shouldBe 1
    createCalls[1] shouldBe 1
    releaseCalls[1] shouldBe 1
  }
}
