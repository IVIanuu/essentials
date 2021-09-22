package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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