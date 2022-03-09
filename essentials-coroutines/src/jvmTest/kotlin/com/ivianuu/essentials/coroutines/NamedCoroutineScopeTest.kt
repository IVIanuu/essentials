/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.*
import com.ivianuu.essentials.di.*
import io.kotest.matchers.booleans.*
import io.kotest.matchers.types.*
import kotlinx.coroutines.*
import org.junit.*

class NamedCoroutineScopeTest {
  @Test fun testNamedCoroutineScopeLifecycle() {
    val scope = Scope<AppScope>()
    val coroutineScope = buildInstance<NamedCoroutineScope<AppScope>> {
      provide { scope }
      namedCoroutineScope()
    }
    coroutineScope.isActive.shouldBeTrue()
    scope.dispose()
    coroutineScope.isActive.shouldBeFalse()
  }

  @OptIn(ExperimentalStdlibApi::class)
  @Test fun testCanSpecifyCustomCoroutineContext() {
    val scope = Scope<AppScope>()
    val customContext = NamedCoroutineContext<AppScope>(Dispatchers.Main)
    val coroutineScope = buildInstance<NamedCoroutineScope<AppScope>> {
      provide { scope }
      provide { customContext }
      namedCoroutineScope()
    }
    coroutineScope.coroutineContext.minusKey(Job.Key) shouldBeSameInstanceAs customContext.value
  }
}
