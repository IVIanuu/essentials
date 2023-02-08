/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <T> Iterable<suspend () -> T>.par(
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): List<T> = coroutineScope {
  val semaphore = Semaphore(concurrency.value)
  map { block ->
    async(context) {
      semaphore.withPermit {
        block()
      }
    }
  }.awaitAll()
}

suspend fun <T> par(
  vararg blocks: suspend () -> T,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): List<T> = blocks.asIterable().par(context)

suspend fun <T, R> Iterable<T>.parMap(
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency,
  transform: suspend (T) -> R
): List<R> = map { t -> suspend { transform(t) } }.par(context)

suspend fun <T> Iterable<T>.parForEach(
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency,
  action: suspend (T) -> Unit
) {
  parMap(context) { action(it) }
}

inline class Concurrency(val value: Int)

expect object ConcurrencyModule {
  @Provide val defaultConcurrency: Concurrency
}
