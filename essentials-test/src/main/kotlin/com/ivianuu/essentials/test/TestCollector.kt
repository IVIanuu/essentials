/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

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

context(CoroutineScope)
fun <T> Flow<T>.testCollect(onEach: suspend (T) -> Unit = {}) =
  TestCollector(onEach).also { collector ->
    launch {
      try {
        collect(collector)
      } catch (e: Throwable) {
        collector.error = e
        throw e
      }
    }
  }
