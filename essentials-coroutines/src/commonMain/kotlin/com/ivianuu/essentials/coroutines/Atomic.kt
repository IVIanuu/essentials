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

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface Atomic<T> {
  suspend fun get(): T

  suspend fun set(value: T)

  suspend fun compareAndSet(expect: T, update: T): Boolean
}

fun <T> Atomic(initial: T): Atomic<T> = AtomicImpl(initial)

private class AtomicImpl<T>(initial: T) : Atomic<T> {
  private var value = initial

  private val lock = Mutex()

  override suspend fun get(): T = lock.withLock { value }

  override suspend fun set(value: T) = lock.withLock { this.value = value }

  override suspend fun compareAndSet(expect: T, update: T): Boolean = lock.withLock {
    if (value == expect) {
      value = update
      true
    } else {
      false
    }
  }
}

suspend inline fun <T> Atomic<T>.update(transform: (T) -> T): T {
  while (true) {
    val prevValue = get()
    val newValue = transform(prevValue)
    if (compareAndSet(prevValue, newValue))
      return newValue
  }
}

suspend inline fun <T> Atomic<T>.getAndUpdate(transform: (T) -> T): T {
  while (true) {
    val prevValue = get()
    val nextValue = transform(prevValue)
    if (compareAndSet(prevValue, nextValue))
      return prevValue
  }
}
