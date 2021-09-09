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

package com.ivianuu.essentials.optics

import io.kotest.matchers.shouldBe
import org.junit.Test

class LensTest {
  private data class Foo(val value: Int)
  private data class Bar(val foo: Foo)

  private val fooLens = Lens<Foo, Int>(get = { it.value }, set = { a, b -> a.copy(value = b) })
  private val barLens = Lens<Bar, Foo>(get = { it.foo }, set = { a, b -> a.copy(foo = b) })

  @Test fun testLens() {
    var foo = Foo(0)
    fooLens.get(foo) shouldBe 0
    foo = fooLens.set(foo, 1)
    foo.value shouldBe 1
  }

  @Test fun testUpdate() {
    var foo = Foo(0)
    foo = fooLens.update(foo) { inc() }
    foo.value shouldBe 1
  }

  @Test fun testPlus() {
    var bar = Bar(Foo(0))
    val barFooValueLens = barLens + fooLens
    bar = barFooValueLens.set(bar, 1)
    barFooValueLens.get(bar) shouldBe 1
  }
}
