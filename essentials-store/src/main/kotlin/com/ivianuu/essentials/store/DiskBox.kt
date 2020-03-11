/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.shareIn
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.measureTimedValue

interface DiskBox<T> : Box<T> {
    interface Serializer<T> {
        fun deserialize(serialized: String): T
        fun serialize(value: T): String
    }
}

fun <T> DiskBox(
    path: String,
    serializer: DiskBox.Serializer<T>,
    defaultValue: T,
    coroutineScope: CoroutineScope
): DiskBox<T> =
    DiskBoxImpl(
        path = path,
        defaultValue = defaultValue,
        serializer = serializer,
        coroutineScope = coroutineScope
    )

internal class DiskBoxImpl<T>(
    private val path: String,
    override val defaultValue: T,
    private val serializer: DiskBox.Serializer<T>,
    coroutineScope: CoroutineScope
) : DiskBox<T> {

    private val changeNotifier = EventFlow<Unit>()
    private val cachedValue = AtomicReference<Any?>(this)
    private val valueFetcher = MutexValue {
        val serialized = try {
            file.bufferedReader().use {
                it.readText()
            }
        } catch (e: Exception) {
            throw IOException("Couldn't read file at $path", e)
        }

        log { "$path -> fetched serialized '$serialized'" }

        return@MutexValue try {
            val deserialized = serializer.deserialize(serialized)
            log { "$path -> deserialized to $deserialized" }
            deserialized
        } catch (e: Exception) {
            throw RuntimeException("Couldn't deserialize '$serialized' for file $path", e)
        }
    }
    private val writeLock = WriteLock(path)

    private val file by lazy { File(path) }

    private val _value: Flow<T> = changeNotifier
        .map { get() }
        .onStart { emit(get()) }
        .distinctUntilChanged()
        .shareIn(
            scope = coroutineScope,
            cacheSize = 1,
            tag = if (logger != null) "DiskBox:$path" else null
        )
    override val data: Flow<T>
        get() {
            log { "$path -> value" }
            return _value
        }

    init {
        log { "$path -> init" }
        coroutineScope.launch { check(!file.isDirectory) }
    }

    override suspend fun updateData(transform: suspend (T) -> T) {
        val newValue = transform(get())
        log { "$path -> set '$newValue'" }
        measured("set") {
            try {
                writeLock.beginWrite()

                if (!file.exists()) {
                    file.parentFile?.mkdirs()

                    if (!file.createNewFile()) {
                        throw IOException("Couldn't create $path")
                    }
                }

                val serialized = try {
                    serializer.serialize(newValue)
                } catch (e: Exception) {
                    throw RuntimeException(
                        "Couldn't serialize value '$newValue' for file $path",
                        e
                    )
                }

                log { "$path -> write serialized '$serialized'" }

                val tmpFile = File.createTempFile(
                    "new", "tmp", file.parentFile
                )
                try {
                    tmpFile.bufferedWriter().use {
                        it.write(serialized)
                    }
                    if (!tmpFile.renameTo(file)) {
                        throw IOException("Couldn't move tmp file to file $path")
                    }
                } catch (e: Exception) {
                    throw IOException("Couldn't write to file $path '$serialized'", e)
                } finally {
                    tmpFile.delete()
                }

                cachedValue.set(newValue)
                changeNotifier.offer(Unit)
            } finally {
                writeLock.endWrite()
            }
        }
    }

    private suspend fun get(): T {
        return measured("get") {
            writeLock.awaitWrite()
            val cached = cachedValue.get()
            if (cached != this) {
                log { "$path -> return cached '$cached'" }
                return@measured cached as T
            }

            return@measured if (file.exists()) {
                valueFetcher().also {
                    cachedValue.set(it)
                    log { "$path -> return fetched '$it'" }
                }
            } else {
                defaultValue.also {
                    log { "$path -> return default value '$it'" }
                }
            }
        }
    }

    private inline fun <T> measured(tag: String, block: () -> T): T {
        val (result, duration) = measureTimedValue(block)
        log { "$path -> compute '$tag' with result '$result' took ${duration.toLongMilliseconds()} ms" }
        return result
    }
}

private class WriteLock(private val path: String) {

    private var currentLock: CompletableDeferred<Unit>? = null
    private val mutex = Mutex()

    suspend fun awaitWrite() {
        log { "$path -> await write start" }
        val lock = mutex.withLock { currentLock }
        lock?.await()
        log { "$path -> await write end" }
    }

    suspend fun beginWrite() {
        log { "$path -> begin write" }
        awaitWrite()
        mutex.withLock { currentLock = CompletableDeferred() }
    }

    suspend fun endWrite() {
        log { "$path -> end write" }
        val lock = mutex.withLock {
            val tmp = currentLock
            currentLock = null
            tmp
        }

        lock?.complete(Unit)
    }
}

private class MutexValue<T>(private val getter: suspend () -> T) {

    private var currentDeferred: Deferred<T>? = null
    private val mutex = Mutex()

    suspend operator fun invoke(): T {
        var deferred = mutex.withLock { currentDeferred }
        if (deferred == null) {
            deferred = CompletableDeferred()
            mutex.withLock { currentDeferred = deferred }
            try {
                val result = getter.invoke()
                deferred.complete(result)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            } finally {
                @Suppress("DeferredResultUnused")
                mutex.withLock { currentDeferred = null }
            }
        }

        return deferred.await()
    }
}
