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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.IOException

interface DiskBox<T> : Box<T> {
    interface Serializer<T> {
        fun deserialize(serialized: String): T
        fun serialize(data: T): String
    }
}

fun <T> DiskBox(
    createFile: () -> File,
    serializer: DiskBox.Serializer<T>,
    defaultData: T,
    coroutineScope: CoroutineScope
): DiskBox<T> = DiskBoxImpl(
    createFile = createFile,
    defaultData = defaultData,
    serializer = serializer,
    coroutineScope = coroutineScope
)

internal class DiskBoxImpl<T>(
    createFile: () -> File,
    override val defaultData: T,
    private val serializer: DiskBox.Serializer<T>,
    coroutineScope: CoroutineScope
) : DiskBox<T> {

    private val changeNotifier = EventFlow<Unit>()
    private var cachedData: Any? = this
    private val cachedDataMutex = Mutex()
    private val writeLock = WriteLock()

    private val file by lazy(createFile)

    override val data: Flow<T> = changeNotifier
        .map { get() }
        .onStart { emit(get()) }
        .distinctUntilChanged()
        .shareIn(
            scope = coroutineScope,
            cacheSize = 1
        )

    override suspend fun updateData(transform: suspend (T) -> T): T {
        val currentData = get()
        val newData = transform(currentData)
        if (newData == currentData) return newData

        try {
            writeLock.beginWrite()

            if (!file.exists()) {
                file.parentFile?.mkdirs()

                if (!file.createNewFile()) {
                    throw IOException("Couldn't create '$file'")
                }
            }

            val serialized = try {
                serializer.serialize(newData)
            } catch (e: Exception) {
                throw RuntimeException(
                    "Couldn't serialize data '$newData' for file '$file'",
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
                    throw IOException("Couldn't move tmp file to file '$file'")
                }
            } catch (e: Exception) {
                throw IOException("Couldn't write to file '$file' '$serialized'", e)
            } finally {
                tmpFile.delete()
            }

            cachedDataMutex.withLock { cachedData = newData }
            changeNotifier.offer(Unit)
        } finally {
            writeLock.endWrite()
        }

        return newData
    }

    private suspend fun get(): T {
        writeLock.awaitWrite()
        val cached = cachedDataMutex.withLock { cachedData }
        if (cached != this) return cached as T

        return if (file.exists()) {
            val serialized = try {
                file.bufferedReader().use {
                    it.readText()
                }
            } catch (e: Exception) {
                throw IOException("Couldn't read file at '$file'", e)
            }

            try {
                val deserialized = serializer.deserialize(serialized)
                cachedDataMutex.withLock { cachedData = deserialized }
                deserialized
            } catch (e: Exception) {
                throw RuntimeException("Couldn't deserialize '$serialized' for file '$file'", e)
            }
        } else {
            defaultData
        }
    }
}

private class WriteLock {

    private var currentLock: CompletableDeferred<Unit>? = null
    private val mutex = Mutex()

    suspend fun awaitWrite() {
        val lock = mutex.withLock { currentLock }
        lock?.await()
    }

    suspend fun beginWrite() {
        awaitWrite()
        mutex.withLock { currentLock = CompletableDeferred() }
    }

    suspend fun endWrite() {
        val lock = mutex.withLock {
            val tmp = currentLock
            currentLock = null
            tmp
        }

        lock?.complete(Unit)
    }
}
