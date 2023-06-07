/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Inject

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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

fun <K, T> RefCountedResource(
  timeout: Duration,
  create: suspend (K) -> T,
  release: (suspend (K, T) -> Unit)? = null,
  @Inject scope: CoroutineScope
): RefCountedResource<K, T> = RefCountedReleaseImpl(create, release, timeout, scope)

suspend inline fun <K, T, R> RefCountedResource<K, T>.withResource(
  key: K,
  crossinline block: suspend (T) -> R
): R = bracket(
  acquire = { acquire(key) },
  use = block,
  release = { _, _ -> release(key) }
)

private class RefCountedReleaseImpl<K, T>(
  private val create: suspend (K) -> T,
  private val release: (suspend (K, T) -> Unit)?,
  private val timeout: Duration,
  private val scope: CoroutineScope?
) : RefCountedResource<K, T> {
  private val itemsLock = Mutex()
  private val items = mutableMapOf<K, Item>()

  override suspend fun acquire(key: K): T {
    val item = itemsLock.withLock {
      items.getOrPut(key) { Item() }
        .also { it.refCount++ }
    }

    return item.getOrCreateValue { create(key) }
  }

  override suspend fun release(key: K) {
    suspend fun releaseImpl() {
      itemsLock.withLock {
        val item = items[key] ?: return@withLock null
        item.refCount--
        if (item.refCount == 0) items.remove(key) else null
      }?.let { removedItem ->
        removedItem.release()
        if (release != null)
          withContext(NonCancellable) {
            val value = removedItem.valueOrThis()
            if (value !== removedItem)
              release.invoke(key, value as T)
          }
      }
    }

    if (timeout == Duration.ZERO) releaseImpl()
    else scope!!.launch {
      delay(timeout)
      releaseImpl()
    }
  }

  private inner class Item(var refCount: Int = 0) {
    private val context = Job()

    private var _value: Any? = this
    private val valueLock = Mutex()

    suspend fun getOrCreateValue(create: suspend () -> T): T = valueLock.withLock {
      val value = _value
      return@withLock if (value !== this) value as T
      else withContext(context) {
        create().also { _value = it }
      }
    }

    suspend fun valueOrThis(): Any? = valueLock.withLock { _value }

    fun release() {
      context.cancel()
    }
  }
}
