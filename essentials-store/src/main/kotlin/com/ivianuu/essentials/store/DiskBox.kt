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
import com.ivianuu.essentials.coroutines.withLockNoInline
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.measureTimedValue

interface DiskBox<T> : Box<T> {
    val path: String

    suspend fun fetch(): T

    interface Serializer<T> {
        fun deserialize(serialized: String): T
        fun serialize(value: T): String
    }
}

fun <T> DiskBox(
    path: String,
    serializer: DiskBox.Serializer<T>,
    defaultValue: T,
    dispatcher: CoroutineDispatcher? = null
): DiskBox<T> =
    DiskBoxImpl(
        path = path,
        defaultValue = defaultValue,
        serializer = serializer,
        dispatcher = dispatcher
    )

internal class DiskBoxImpl<T>(
    override val path: String,
    override val defaultValue: T,
    private val serializer: DiskBox.Serializer<T>,
    private val dispatcher: CoroutineDispatcher? = null
) : DiskBox<T> {

    private val _isDisposed = AtomicBoolean(false)
    override val isDisposed: Boolean
        get() = _isDisposed.get()

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

        log { "$path -> fetched raw $serialized" }

        return@MutexValue try {
            val deserialized = serializer.deserialize(serialized)
            log { "$path -> deserialized to $deserialized" }
            deserialized
        } catch (e: Exception) {
            throw RuntimeException("Couldn't deserialize '$serialized' for file $path", e)
        }
    }
    private val writeLock = WriteLock(path)

    private val coroutineScope = CoroutineScope(Job())

    private val file by lazy { File(path) }

    private val multiInstanceHelper =
        MultiInstanceHelper(coroutineScope, path) {
            log { "$path -> multi instance change force refetch" }
            cachedValue.set(this) // force refetching the value
            changeNotifier.offer(Unit)
        }

    private val flow: Flow<T> = changeNotifier
        .map { get() }
        .onStart { emit(get()) }
        .distinctUntilChanged()
        .shareIn(
            scope = coroutineScope,
            cacheSize = 1,
            tag = if (logger != null) "DiskBox:$path" else null
        )

    init {
        log { "$path -> init" }
        coroutineScope.launch {
            maybeWithDispatcher {
                check(!file.isDirectory)
            }
        }
    }

    override suspend fun set(value: T) {
        checkNotDisposed()
        log { "$path -> set $value" }
        maybeWithDispatcher {
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
                        serializer.serialize(value)
                    } catch (e: Exception) {
                        throw RuntimeException(
                            "Couldn't serialize value '$value' for file $path",
                            e
                        )
                    }

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
                        throw IOException("Couldn't write to file $path $serialized", e)
                    } finally {
                        tmpFile.delete()
                    }

                    cachedValue.set(value)
                    changeNotifier.offer(Unit)
                    multiInstanceHelper.notifyWrite()
                } finally {
                    writeLock.endWrite()
                }
            }
        }
    }

    override suspend fun get(): T {
        checkNotDisposed()
        log { "$path -> get" }
        return maybeWithDispatcher {
            measured("get") {
                writeLock.awaitWrite()
                val cached = cachedValue.get()
                if (cached != this) {
                    log { "$path -> return cached $cached" }
                    return@measured cached as T
                }

                return@measured if (isSet()) {
                    valueFetcher().also {
                        cachedValue.set(it)
                        log { "$path -> return fetched $it" }
                    }
                } else {
                    defaultValue.also {
                        log { "$path -> return default value $it" }
                    }
                }
            }
        }
    }

    override suspend fun fetch(): T {
        checkNotDisposed()
        log { "$path -> fetch" }
        return maybeWithDispatcher {
            measured("fetch") {
                cachedValue.set(this)
                val result = get()
                changeNotifier.offer(Unit)
                return@measured result
            }
        }
    }

    override suspend fun delete() {
        checkNotDisposed()
        log { "$path -> delete" }
        maybeWithDispatcher {
            measured("delete") {
                if (file.exists()) {
                    if (!file.delete()) {
                        throw IOException("Couldn't delete file $path")
                    }
                }

                cachedValue.set(this)
                changeNotifier.offer(Unit)
            }
        }
    }

    override suspend fun isSet(): Boolean {
        checkNotDisposed()
        return maybeWithDispatcher {
            measured("exists") {
                file.exists().also {
                    log { "$path -> exists $it" }
                }
            }
        }
    }

    override fun asFlow(): Flow<T> {
        checkNotDisposed()
        log { "$path -> as flow" }
        return flow
    }

    override fun dispose() {
        log { "$path -> dispose" }
        if (_isDisposed.getAndSet(true)) {
            coroutineScope.coroutineContext[Job.Key]?.cancel()
        }
    }

    private suspend fun <T> maybeWithDispatcher(block: suspend () -> T): T =
        if (dispatcher != null) withContext(dispatcher) { block() } else block()

    private fun checkNotDisposed() {
        require(!_isDisposed.get()) { "Box is already disposed" }
    }

    private inline fun <T> measured(tag: String, block: () -> T): T {
        val (result, duration) = measureTimedValue(block)
        log { "$path -> compute '$tag' with result '$result' took ${duration.toLongMilliseconds()} ms" }
        return result
    }
}

private class MultiInstanceHelper(
    coroutineScope: CoroutineScope,
    private val path: String,
    private val onChange: () -> Unit
) {

    private val id = UUID.randomUUID().toString()

    init {
        coroutineScope.launch {
            changeNotifier
                .onEach { change ->
                    if (change.path == path && change.id != id) {
                        onChange()
                    }
                }
        }
    }

    fun notifyWrite() {
        changeNotifier.offer(Change(id = id, path = path))
    }

    private companion object {
        private val changeNotifier = EventFlow<Change>()
    }

    private class Change(
        val id: String,
        val path: String
    )
}

private class WriteLock(private val path: String) {

    private var currentLock: CompletableDeferred<Unit>? = null
    private val mutex = Mutex()

    suspend fun awaitWrite() {
        log { "$path -> await write start" }
        val lock = mutex.withLockNoInline { currentLock }
        lock?.await()
        log { "$path -> await write end" }
    }

    suspend fun beginWrite() {
        log { "$path -> begin write" }
        awaitWrite()
        mutex.withLockNoInline { currentLock = CompletableDeferred() }
    }

    suspend fun endWrite() {
        log { "$path -> end write" }
        val lock = mutex.withLockNoInline {
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
        var deferred = mutex.withLockNoInline { currentDeferred }
        if (deferred == null) {
            deferred = CompletableDeferred()
            mutex.withLockNoInline { currentDeferred = deferred }
            try {
                val result = getter.invoke()
                deferred.complete(result)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            } finally {
                @Suppress("DeferredResultUnused")
                mutex.withLockNoInline { currentDeferred = null }
            }
        }

        return deferred.await()
    }
}
