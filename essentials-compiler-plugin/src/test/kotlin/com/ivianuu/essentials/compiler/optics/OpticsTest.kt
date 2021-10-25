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

package com.ivianuu.essentials.compiler.optics

import com.ivianuu.essentials.compiler.codegen
import com.ivianuu.essentials.compiler.invokeSingleFile
import io.kotest.matchers.shouldBe
import org.junit.Test

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
