/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlin.coroutines.*

suspend fun <T> par(
  vararg blocks: suspend () -> T,
  context: CoroutineContext = EmptyCoroutineContext,
  concurrency: Int = DEFAULT_CONCURRENCY
): List<T> = coroutineScope {
  val semaphore = Semaphore(concurrency)
  blocks.map { block ->
    async(context) {
      semaphore.withPermit {
        block()
      }
    }
  }.awaitAll()
}

suspend fun <T, R> Iterable<T>.parMap(
  context: CoroutineContext = EmptyCoroutineContext,
  concurrency: Int = DEFAULT_CONCURRENCY,
  transform: suspend (T) -> R
): List<R> = coroutineScope {
  val semaphore = Semaphore(concurrency)
  map { item ->
    async(context) {
      semaphore.withPermit { transform(item) }
    }
  }.awaitAll()
}

suspend fun <T> Iterable<T>.parForEach(
  context: CoroutineContext = EmptyCoroutineContext,
  concurrency: Int = DEFAULT_CONCURRENCY,
  action: suspend (T) -> Unit
) {
  parMap(context, concurrency) { action(it) }
}

const val DEFAULT_CONCURRENCY = 64
