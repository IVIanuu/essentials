/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.di

class ElementsTest {
  /*@Test fun testElements() {
    @Provide val int = Element(TestScope, 42)
    @Provide val string = Element(TestScope, "42")

    val elements = buildInstance<Elements<TestScope>>()

    elements<Int>() shouldBe 42
    elements<String>() shouldBe "42"
  }

  @Test fun testEager() {
    var callCount = 0

    class Foo

    @Provide val eagerFooModule = EagerModule(TestScope) {
      callCount++
      Foo()
    }

    @Provide val scope = Scope<TestScope>()
    @Provide val elements = buildInstance<Elements<TestScope>>()
    callCount shouldBe 1
    val a = buildInstance<Foo>()
    callCount shouldBe 1
    val b = buildInstance<Foo>()
    callCount shouldBe 1
    a shouldBeSameInstanceAs b
  }*/
}