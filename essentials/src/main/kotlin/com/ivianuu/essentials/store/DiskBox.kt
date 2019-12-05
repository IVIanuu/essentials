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

import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.essentials.util.share
import com.ivianuu.scopes.MutableScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.measureTimedValue

interface DiskBox<T> : Box<T> {
    val path: String

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
): DiskBox<T> = DiskBoxImpl(
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

    private val changeNotifier = BroadcastChannel<Unit>(1)
    private val cachedValue = AtomicReference<Any?>(this)
    private val valueFetcher = MutexValue {
        try {
            val serialized = file.bufferedReader().use {
                it.readText()
            }
            return@MutexValue serializer.deserialize(serialized)
        } catch (e: Exception) {
            throw IOException("Couldn't read file $file", e)
        }
    }

    private val scope = MutableScope()

    private val file by lazy { File(path) }

    private val sharedFlow by lazy {
        file.changes()
            .onStart { d { "$path -> flow start" } }
            .onEach { d { "$path -> on file change" } }
            .onCompletion { d { "$path -> flow end" } }
            .filter {
                !selfChange.getAndSet(false)
                    .also { isSelfChange ->
                        d { "$path -> file changed is self ? $isSelfChange" }
                    }
            }
            .onEach {
                d { "$path -> reset cache due to external change" }
                // force refetching the value
                cachedValue.set(this)
            }
            .flatMapLatest {
                changeNotifier.asFlow()
                    .onEach { d { "$path -> change notified" } }
            }
            .onEach { d { "$path -> refresh stream" } }
            .map { get() }
            .onEach { d { "$path -> deliver flow value $it" } }
            .share(scope.coroutineScope)
    }

    private val selfChange = AtomicBoolean(false)

    init {
        d { "$path -> init" }
        scope.coroutineScope.launch {
            maybeWithDispatcher {
                check(!file.isDirectory)
            }
        }
    }

    override suspend fun set(value: T) {
        checkNotDisposed()
        d { "$path -> set $value" }
        maybeWithDispatcher {
            measured("set") {
                if (!file.exists()) {
                    file.parentFile?.mkdirs()

                    if (!file.createNewFile()) {
                        throw IOException("Couldn't create $file")
                    }
                }

                val serialized = serializer.serialize(value)

                val tmpFile = File.createTempFile(
                    "new", "tmp", file.parentFile
                )
                try {
                    tmpFile.bufferedWriter().use {
                        it.write(serialized)
                    }
                    if (!tmpFile.renameTo(file)) {
                        throw IOException("Couldn't move tmp file to file $file")
                    }
                } catch (e: Exception) {
                    throw IOException("Couldn't write to file $file $serialized", e)
                } finally {
                    tmpFile.delete()
                }

                cachedValue.set(value)
                selfChange.set(true)
                changeNotifier.offer(Unit)
            }
        }
    }

    override suspend fun get(): T {
        checkNotDisposed()
        d { "$path -> get" }
        return maybeWithDispatcher {
            measured("get") {
                val cached = cachedValue.get()
                if (cached != this) {
                    d { "$path -> return cached $cached" }
                    return@measured cached as T
                }

                return@measured if (isSet()) {
                    valueFetcher().also {
                        cachedValue.set(it)
                        d { "$path -> return fetched $it" }
                    }
                } else {
                    defaultValue.also {
                        d { "$path -> return default value $it" }
                    }
                }
            }
        }
    }

    override suspend fun delete() {
        checkNotDisposed()
        d { "$path -> delete" }
        maybeWithDispatcher {
            measured("delete") {
                if (file.exists()) {
                    if (!file.delete()) {
                        throw IOException("Couldn't delete file $file")
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
                    d { "$path -> exists $it" }
                }
            }
        }
    }

    override fun asFlow(): Flow<T> {
        d { "$path -> as flow" }
        checkNotDisposed()
        return sharedFlow
            .onStart { emit(get()) }
    }

    override fun dispose() {
        d { "$path -> dispose" }
        if (_isDisposed.getAndSet(true)) {
            scope.close()
        }
    }

    private suspend fun <T> maybeWithDispatcher(block: suspend () -> T): T =
        if (dispatcher != null) withContext(dispatcher) { block() } else block()

    private fun checkNotDisposed() {
        require(!_isDisposed.get()) { "Box is already disposed" }
    }

    private inline fun <T> measured(tag: String, block: () -> T): T {
        val (result, duration) = measureTimedValue(block)
        d { "$path -> compute '$tag' with result '$result' took ${duration.toLongMilliseconds()} ms" }
        return result
    }

}

private fun File.changes(): Flow<Unit> = flow {
    var lastFileState = getState()
    while (true) {
        val currentFileState = getState()
        if (lastFileState != currentFileState) {
            lastFileState = currentFileState
            emit(Unit)
        }
        delay(100)
    }
}

private fun File.getState() = FileState(
    exists = exists(),
    lastModified = lastModified()
)

private data class FileState(
    var exists: Boolean,
    var lastModified: Long
)

private class MutexValue<T>(private val getter: suspend () -> T) {

    private var currentDeferred: Deferred<T>? = null
    private val deferredLock = Any()

    suspend operator fun invoke(): T {
        var deferred = synchronized(deferredLock) { currentDeferred }
        if (deferred == null) {
            deferred = CompletableDeferred()
            synchronized(deferredLock) { currentDeferred = deferred }
            try {
                val result = getter.invoke()
                deferred.complete(result)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            } finally {
                @Suppress("DeferredResultUnused")
                synchronized(deferredLock) { currentDeferred = null }
            }
        }

        return deferred.await()
    }
}
