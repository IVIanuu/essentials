/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.ivianuu.injekt.common.*
import io.kotest.matchers.*
import org.junit.*

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
