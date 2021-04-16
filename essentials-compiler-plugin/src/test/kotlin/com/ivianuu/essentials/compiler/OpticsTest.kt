package com.ivianuu.essentials.compiler

import io.kotest.matchers.shouldBe
import org.junit.Test

class OpticsTest {
    @Test
    fun testOptics() = codegen(
        """
            import com.ivianuu.essentials.optics.*
            @Optics data class MyClass(val value: String)
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

    @Test
    fun testOpticsWithGenerics() = codegen(
        """
            import com.ivianuu.essentials.optics.*
            @Optics data class MyClass<A>(val value: A)
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
