/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Duration

interface RefCountedResource<K, T> {
  suspend fun acquire(key: K): T

  suspend fun release(key: K)
}

fun <K, T> RefCountedResource(
  create: suspend (K) -> T,
  release: (suspend (K, T) -> Unit)? = null
): RefCountedResource<K, T> = RefCountedReleaseImpl(create, release, Duration.ZERO, null)

context(CoroutineScope) fun <K, T> RefCountedResource(
  timeout: Duration,
  create: suspend (K) -> T,
  release: (suspend (K, T) -> Unit)? = null,
): RefCountedResource<K, T> = RefCountedReleaseImpl(create, release, timeout, this@CoroutineScope)

private class RefCountedReleaseImpl<K, T>(
  private val create: suspend (K) -> T,
  private val release: (suspend (K, T) -> Unit)?,
  private val timeout: Duration,
  private val scope: CoroutineScope?
) : RefCountedResource<K, T> {
  private val lock = Mutex()
  private val values = mutableMapOf<K, Item>()

  override suspend fun acquire(key: K) = lock.withLock {
    values.getOrPut(key) { Item(withContext(NonCancellable) { create(key) }, 0) }
      .also { it.refCount++ }
  }.value

  override suspend fun release(key: K) {
    suspend fun releaseImpl() {
      lock.withLock {
        val item = values[key] ?: return@withLock null
        item.refCount--
        if (item.refCount == 0) values.remove(key) else null
      }?.let { removedItem ->
        if (release != null)
          withContext(NonCancellable) {
            release.invoke(key, removedItem.value)
          }
      }
    }

    if (timeout == Duration.ZERO) releaseImpl()
    else scope!!.launch {
      delay(timeout)
      releaseImpl()
    }
  }

  private inner class Item(val value: T, var refCount: Int = 0)
}

suspend inline fun <K, T, R> RefCountedResource<K, T>.withResource(
  key: K,
  crossinline block: suspend (T) -> R
): R = bracket(
  acquire = { acquire(key) },
  use = block,
  release = { _, _ -> release(key) }
)
