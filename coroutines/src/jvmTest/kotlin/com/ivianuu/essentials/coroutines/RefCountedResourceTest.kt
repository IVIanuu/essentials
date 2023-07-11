/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.time.milliseconds
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RefCountedResourceTest {
  @Test fun testRefCountedResource() = runCancellingBlockingTest {
    class Resource(val key: Int, var released: Boolean)

    val refCountedResource = RefCountedResource<Int, Resource>(
      create = { Resource(it, false) },
      release = { _, r -> r.released = true }
    )

    val resourceA1 = refCountedResource.acquire(1)
    resourceA1.key shouldBe 1
    resourceA1.released shouldBe false

    val resourceA2 = refCountedResource.acquire(1)
    resourceA1 shouldBeSameInstanceAs resourceA2

    refCountedResource.release(1)

    resourceA1.released shouldBe false

    refCountedResource.release(1)

    resourceA1.released shouldBe true

    refCountedResource.withResource(1) {
      it shouldNotBeSameInstanceAs resourceA1
    }

    refCountedResource.withResource(2) {
      it.key shouldBe 2
      it shouldNotBeSameInstanceAs resourceA1
    }
  }

  @Test fun testRefCountedResourceWithTimeout() = runBlocking {
    class Resource(val key: Int, var released: Boolean)

    val refCountedResource = RefCountedResource<Int, Resource>(
      timeout = 100.milliseconds,
      create = { Resource(it, false) },
      release = { _, r -> r.released = true }
    )

    val resource = refCountedResource.acquire(1)
    resource.released shouldBe false
    refCountedResource.release(1)
    resource.released shouldBe false
    delay(50)
    refCountedResource.acquire(1)
    delay(100)
    resource.released shouldBe false
    refCountedResource.release(1)
    delay(150)
    resource.released shouldBe true
  }
}
