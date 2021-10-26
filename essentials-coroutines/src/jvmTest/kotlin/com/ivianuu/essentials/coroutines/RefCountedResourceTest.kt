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
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
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
}