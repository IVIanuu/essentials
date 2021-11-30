/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun <T> Flow<T>.onCancel(action: suspend FlowCollector<T>.() -> Unit): Flow<T> = object : Flow<T> {
  override suspend fun collect(collector: FlowCollector<T>) {
    try {
      this@onCancel.collect { value ->
        try {
          collector.emit(value)
        } catch (e: CancellationException) {
          throw CollectorCancellationException(e)
        }
      }
    } catch (e: CancellationException) {
      if (e !is CollectorCancellationException) {
        withContext(NonCancellable) {
          action(collector)
        }
      }
      throw e
    }
  }
}

private class CollectorCancellationException(
  override val cause: CancellationException
) : CancellationException(null)
