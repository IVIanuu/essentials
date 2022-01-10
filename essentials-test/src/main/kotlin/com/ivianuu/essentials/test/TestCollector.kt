/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.test

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TestCollector<T>(
  private val onEach: suspend (T) -> Unit = {}
) : FlowCollector<T> {
  val values = mutableListOf<T>()
  var error: Throwable? = null
  override suspend fun emit(value: T) {
    values += value
    onEach(value)
  }
}

fun <T> Flow<T>.testCollect(scope: CoroutineScope, onEach: suspend (T) -> Unit = {}) =
  TestCollector(onEach).also { collector ->
    scope.launch {
      try {
        collect(collector)
      } catch (e: Throwable) {
        collector.error = e
        throw e
      }
    }
  }
