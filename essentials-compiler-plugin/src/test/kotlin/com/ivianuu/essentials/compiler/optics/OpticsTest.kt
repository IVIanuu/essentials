/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.compiler.optics

import com.ivianuu.essentials.compiler.*
import io.kotest.matchers.*
import org.junit.*

class OpticsTest {
  @Test fun testOptics() = codegen(
    """
      @com.ivianuu.essentials.optics.Optics data class MyClass(val value: String)
      
      fun invoke(): String {
        val valueLens = MyClass.value()
        val initial = MyClass("hello")
        val modified = valueLens.set(initial, valueLens.get(initial) + " world")
        return valueLens.get(modified)
      }
    """
  ) {
    invokeSingleFile() shouldBe "hello world"
  }

  @Test fun testOpticsWithGenerics() = codegen(
    """
      @com.ivianuu.essentials.optics.Optics data class MyClass<A>(val value: A)
      
      fun invoke(): String {
        val valueLens = MyClass.value<String>()
        val initial = MyClass("hello")
        val modified = valueLens.set(initial, valueLens.get(initial) + " world")
        return valueLens.get(modified)
      }
    """
  ) {
    invokeSingleFile() shouldBe "hello world"
  }
}
