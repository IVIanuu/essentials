/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import kotlin.jvm.JvmInline

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

@JvmInline value class Concurrency(val value: Int)

@Provide internal expect val defaultConcurrency: Concurrency
