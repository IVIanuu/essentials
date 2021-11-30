/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.sync.*

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
