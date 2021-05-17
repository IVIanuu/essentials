/*
 * Copyright 2020 Manuel Wrage
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

import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

suspend fun <T> par(
  vararg blocks: suspend () -> T,
  @Given concurrency: Concurrency
): List<T> = blocks.asIterable().parMap { it() }

suspend fun <T, R> Iterable<T>.parMap(
  @Given concurrency: Concurrency,
  transform: suspend (T) -> R
): List<R> = supervisorScope {
  val semaphore = Semaphore(concurrency.value)
  map { item ->
    async {
      semaphore.acquire()
      try {
        transform(item)
      } finally {
        semaphore.release()
      }
    }
  }.awaitAll()
}

suspend fun <T> Iterable<T>.parFilter(
  @Given concurrency: Concurrency,
  predicate: suspend (T) -> Boolean
): List<T> = parMap { if (predicate(it)) it else null }.filterNotNull()

suspend fun <T> Iterable<T>.parForEach(
  @Given concurrency: Concurrency,
  action: suspend (T) -> Unit
) {
  parMap { action(it) }
}

inline class Concurrency(val value: Int)

@Given
internal expect val defaultConcurrency: Concurrency
