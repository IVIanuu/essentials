package com.ivianuu.essentials.optics

import io.kotest.matchers.*
import org.junit.*

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
