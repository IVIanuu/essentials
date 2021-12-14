/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlin.coroutines.*

suspend fun <T> par(
  vararg blocks: suspend () -> T,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): List<T> = coroutineScope {
  val semaphore = Semaphore(concurrency.value)
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
  @Inject concurrency: Concurrency,
  transform: suspend (T) -> R
): List<R> = coroutineScope {
  val semaphore = Semaphore(concurrency.value)
  map { item ->
    async(context) {
      semaphore.withPermit { transform(item) }
    }
  }.awaitAll()
}

suspend fun <T> Iterable<T>.parFilter(
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency,
  predicate: suspend (T) -> Boolean
): List<T> = parMap(context) { if (predicate(it)) it else null }.filterNotNull()

suspend fun <T> Iterable<T>.parForEach(
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency,
  action: suspend (T) -> Unit
) {
  parMap(context) { action(it) }
}

inline class Concurrency(val value: Int)

@Provide internal expect val defaultConcurrency: Concurrency
