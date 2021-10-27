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

package com.ivianuu.essentials.cache

import com.ivianuu.essentials.nonFatalOrThrow
import com.ivianuu.essentials.time.Clock
import com.ivianuu.injekt.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

interface Cache<K : Any, V : Any> {
  suspend fun get(key: K): V?

  suspend fun get(key: K, computation: suspend (K) -> V): V

  suspend fun put(key: K, value: V)

  suspend fun remove(key: K)

  suspend fun removeAll()

  suspend fun asMap(): Map<K, V>
}

suspend fun <K : Any> Cache<K, *>.contains(key: K): Boolean = get(key) != null

suspend fun Cache<*, *>.size(): Int = asMap().size

fun <K : Any, V : Any> Cache(
  expireAfterWriteDuration: Duration = Duration.INFINITE,
  expireAfterAccessDuration: Duration = Duration.INFINITE,
  maxSize: Long = Long.MAX_VALUE,
  @Inject clock: Clock
): Cache<K, V> = CacheImpl(expireAfterWriteDuration, expireAfterAccessDuration, maxSize, clock)

internal class CacheImpl<K : Any, V : Any>(
  private val expireAfterWriteDuration: Duration,
  private val expireAfterAccessDuration: Duration,
  private val maxSize: Long = Long.MAX_VALUE,
  @Inject val clock: Clock
) : Cache<K, V> {
  private val cacheLock = Mutex()

  private val entries = hashMapOf<K, CacheEntry>()

  private val writeQueue = if (expireAfterWriteDuration.isFinite())
    mutableSetOf<CacheEntry>() else null
  private val accessQueue = if (expireAfterAccessDuration.isFinite() || maxSize < Long.MAX_VALUE)
    mutableSetOf<CacheEntry>() else null

  override suspend fun get(key: K): V? {
    val now = clock()
    return cacheLock.withLock {
      removeExpiredEntries(now)

      entries[key]
        ?.also { it.recordRead(now) }
    }?.awaitValue(now)
  }

  override suspend fun get(key: K, computation: suspend (K) -> V): V {
    val now = clock()
    return cacheLock.withLock {
      removeExpiredEntries(now)

      val entry = entries[key] ?: CacheEntry(key)
        .also { entries[key] = it }

      trimEntries()

      entry
    }.run {
      coroutineScope {
        awaitValueOrUpdateFrom(computation)
      }
    }
  }

  override suspend fun put(key: K, value: V) {
    val now = clock()
    cacheLock.withLock {
      removeExpiredEntries(now)

      val entry = entries[key] ?: CacheEntry(key)
        .also { entries[key] = it }

      trimEntries()

      entry
    }.updateValueImmediately(value, now)
  }

  override suspend fun remove(key: K) {
    val now = clock()
    cacheLock.withLock {
      removeExpiredEntries(now)

      entries.remove(key)?.also {
        writeQueue?.remove(it)
        accessQueue?.remove(it)
        it.onRemove()
      }
    }
  }

  override suspend fun removeAll(): Unit = cacheLock.withLock {
    entries.forEach { it.value.onRemove() }
    entries.clear()
    writeQueue?.clear()
    accessQueue?.clear()
  }

  override suspend fun asMap(): Map<K, V> {
    val now = clock()
    return cacheLock.withLock {
      buildMap {
        this@CacheImpl.entries.forEach { (_, entry) ->
          entry.valueSnapshot(now)
            ?.let { this[entry.key] = it }
        }
      }
    }
  }

  private suspend fun removeExpiredEntries(now: Duration) {
    writeQueue?.expireEntries { now - it.writeTime >= expireAfterWriteDuration }
    accessQueue?.expireEntries { now - it.accessTime >= expireAfterAccessDuration }
  }

  private suspend inline fun MutableSet<CacheEntry>.expireEntries(predicate: (CacheEntry) -> Boolean) {
    val iterator = iterator()
    for (entry in iterator) {
      if (!predicate(entry)) break
      entries.remove(entry.key)
      iterator.remove()
      if (this === writeQueue) accessQueue?.remove(entry)
      else writeQueue?.remove(entry)
      entry.onRemove()
    }
  }

  private suspend fun trimEntries() {
    if (maxSize == Long.MAX_VALUE) return

    while (entries.size > maxSize) {
      val entryToEvict = accessQueue!!.firstOrNull() ?: continue
      entries.remove(entryToEvict.key)?.onRemove()
      writeQueue?.remove(entryToEvict)
      accessQueue.remove(entryToEvict)
    }
  }

  private fun <E> MutableSet<E>.addOrMove(element: E) {
    remove(element)
    add(element)
  }

  private inner class CacheEntry(val key: K) {
    var accessTime = Duration.INFINITE
      private set
    var writeTime = Duration.INFINITE
      private set

    private val valueLock = Mutex()
    private var value: Deferred<V>? = null

    suspend fun valueSnapshot(now: Duration): V? = valueLock.withLock {
      try {
        value?.getCompleted()
      } catch (e: IllegalStateException) {
        null
      }
    }.also {
      recordRead(now)
    }

    suspend fun awaitValue(now: Duration): V? = try {
        valueLock.withLock { value }?.await()
      } catch (e: Throwable) {
        if (e is ValueOverrideException)
          e.value as V
        else {
          e.nonFatalOrThrow()
          null
        }
      }?.also {
        cacheLock.withLock { recordRead(now) }
      }

    suspend fun awaitValueOrUpdateFrom(block: suspend (K) -> V): V = try {
      valueLock.withLock {
        value ?: CoroutineScope(coroutineContext).async {
          block(key).also {
            val now = clock()
            cacheLock.withLock { recordWrite(now) }
          }
        }.also { value = it }
      }.await()
    } catch (e: CancellationException) {
      if (e is ValueOverrideException) {
        e.value as V
      } else {
        cacheLock.withLock {
          entries.remove(key)?.onRemove()
          writeQueue?.remove(this)
          accessQueue?.remove(this)
        }
        throw e
      }
    }
      .also {
        val now = clock()
        cacheLock.withLock { recordRead(now) }
      }

    suspend fun updateValueImmediately(value: V, now: Duration) {
      valueLock.withLock {
        val previousValue = this.value
        this.value = CompletableDeferred(value)
        previousValue?.cancel(ValueOverrideException(value))
      }
      cacheLock.withLock { recordWrite(now) }
    }

    suspend fun onRemove() {
      valueLock.withLock {
        value?.cancel()
      }
    }

    fun recordRead(now: Duration) {
      if (accessQueue != null) {
        accessTime = now
        accessQueue.addOrMove(this)
      }
    }

    fun recordWrite(now: Duration) {
      recordRead(now)

      if (writeQueue != null) {
        writeTime = now
        writeQueue.addOrMove(this)
      }
    }
  }

  class ValueOverrideException(val value: Any) : CancellationException(null)
}
