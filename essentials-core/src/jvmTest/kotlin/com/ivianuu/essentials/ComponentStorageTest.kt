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

package com.ivianuu.essentials

import com.ivianuu.injekt.common.Disposable
import io.kotest.matchers.shouldBe
import org.junit.Test

class ComponentStorageTest {
  @Test fun testBasic() {
    val storage = ComponentStorageImpl<Any>()

    storage.get<String>(0) shouldBe null
    storage.get<String>(1) shouldBe null

    storage[0] = "a"
    storage.get<String>(0) shouldBe "a"
    storage.get<String>(1) shouldBe null

    storage[1] = "b"
    storage.get<String>(0) shouldBe "a"
    storage.get<String>(1) shouldBe "b"

    storage.remove(0)
    storage.get<String>(0) shouldBe null
    storage.get<String>(1) shouldBe "b"

    storage.remove(1)
    storage.get<String>(0) shouldBe null
    storage.get<String>(1) shouldBe null
  }

  @Test fun testDisposesValueOnRemoval() {
    val storage = ComponentStorageImpl<Any>()

    var disposeCalls = 0

    storage[0] = Disposable { disposeCalls++ }
    disposeCalls shouldBe 0

    storage.remove(0)
    disposeCalls shouldBe 1
  }

  @Test fun testDisposesValueOnDispose() {
    val storage = ComponentStorageImpl<Any>()

    var disposeCalls = 0

    storage[0] = Disposable { disposeCalls++ }
    disposeCalls shouldBe 0

    storage.dispose()
    disposeCalls shouldBe 1
  }
}
