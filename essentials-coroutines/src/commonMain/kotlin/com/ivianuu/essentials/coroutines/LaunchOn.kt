/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

fun <T> Flow<T>.launchOnStart(block: suspend CoroutineScope.() -> Unit) = flow {
  coroutineScope {
    launch(block = block)
    emitAll(this@launchOnStart)
  }
}

fun <T> Flow<T>.launchOnEach(block: suspend CoroutineScope.(T) -> Unit) = flow {
  coroutineScope {
    this@launchOnEach.collect { value ->
      emit(value)
      launch { block(value) }
    }
  }
}

fun <T> Flow<T>.launchOnEachLatest(block: suspend CoroutineScope.(T) -> Unit) = flow {
  coroutineScope {
    var lastJob: Job? = null
    this@launchOnEachLatest.collect { value ->
      lastJob?.cancelAndJoin()
      emit(value)
      lastJob = launch { block(value) }
    }
  }
}

fun <T> Flow<T>.launchOnCompletion(block: suspend CoroutineScope.(Throwable?) -> Unit) = flow {
  coroutineScope {
    emitAll(
      this@launchOnCompletion
        .onCompletion {
          launch {
            block(it)
          }.join()
        }
    )
  }
}

fun <T> Flow<T>.launchOnCancel(block: suspend CoroutineScope.() -> Unit) = flow {
  coroutineScope {
    emitAll(
      this@launchOnCancel
        .onCancel {
          launch {
            block()
          }
        }
    )
  }
}
