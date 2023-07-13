/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Provide
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

context(ConcurrencyContext)  suspend fun <T> Iterable<suspend () -> T>.par(
  context: CoroutineContext = EmptyCoroutineContext
): List<T> = coroutineScope {
  val semaphore = Semaphore(concurrency)
  map { block ->
    async(context) {
      semaphore.withPermit {
        block()
      }
    }
  }.awaitAll()
}

context(ConcurrencyContext) suspend fun <T> par(
  vararg blocks: suspend () -> T,
  context: CoroutineContext = EmptyCoroutineContext
): List<T> = blocks.asIterable().par()

context(ConcurrencyContext) suspend fun <T, R> Iterable<T>.parMap(
  context: CoroutineContext = EmptyCoroutineContext,
  transform: suspend (T) -> R
): List<R> = map { t -> suspend { transform(t) } }.par()

context(ConcurrencyContext) suspend fun <T> Iterable<T>.parForEach(
  context: CoroutineContext = EmptyCoroutineContext,
  action: suspend (T) -> Unit
) {
  parMap(context) { action(it) }
}

@JvmInline value class ConcurrencyContext(val concurrency: Int)

@Provide expect object ConcurrencyModule {
  @Provide val defaultConcurrencyContext: ConcurrencyContext
}
