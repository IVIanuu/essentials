/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.di

import io.kotest.assertions.throwables.*
import io.kotest.matchers.types.*
import org.junit.*

class DiTest {
  @Test fun testBuildInstance() {
    val foo = Foo()
    val result = buildInstance<Bar> {
      provide { foo }
      provide { Bar(get()) }
    }
    result.foo shouldBeSameInstanceAs foo
  }

  @Test fun testBuildChildInstance() {
    val foo = Foo()
    val result = buildInstance<Bar> {
      provide {
        Bar(buildChildInstance { provide { foo } })
      }
    }
    result.foo shouldBeSameInstanceAs foo
  }

  @Test fun testFunctionInjection() {
    val barProvider = buildInstance<(Foo) -> Bar> {
      provide { resolve(::Bar) }
    }

    val fooA = Foo()
    val barA = barProvider(fooA)
    barA.foo shouldBeSameInstanceAs fooA
    val fooB = Foo()
    val barB = barProvider(fooB)
    barB.foo shouldBeSameInstanceAs fooB
  }

  @Test fun testListInjection() {
    val commands = buildInstance<List<Command>> {
      provideIntoList<Command> { CommandA() }
      provideIntoList<Command> { CommandB() }
    }

    commands[0].shouldBeTypeOf<CommandA>()
    commands[1].shouldBeTypeOf<CommandB>()
  }

  @Test fun testListRequestWithoutProvidersDoesNotWork() {
    shouldThrow<NoProviderFoundException> {
      buildInstance<List<Command>> {
      }
    }
  }
}
