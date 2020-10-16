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

package com.ivianuu.essentials.datastore

import com.ivianuu.essentials.coroutines.EventFlow
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

interface Serializer<T> {
    fun deserialize(serializedData: String): T
    fun serialize(data: T): String
}

class SerializerException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)

fun <T> DiskDataStore(
    produceFile: () -> File,
    produceSerializer: () -> Serializer<T>,
    produceDefaultData: () -> T,
    scope: CoroutineScope,
    corruptionHandler: CorruptionHandler = NoopCorruptionHandler
): DataStore<T> = DiskDataStoreImpl(
    produceFile = produceFile,
    produceDefaultData = produceDefaultData,
    produceSerializer = produceSerializer,
    scope = scope,
    corruptionHandler = corruptionHandler
)

internal class DiskDataStoreImpl<T>(
    produceFile: () -> File,
    produceSerializer: () -> Serializer<T>,
    produceDefaultData: () -> T,
    private val scope: CoroutineScope,
    private val corruptionHandler: CorruptionHandler
) : DataStore<T> {

    private val changeNotifier = EventFlow<T>()
    private var cachedData: Any? = this
    private val cachedDataMutex = Mutex()
    private val writeLock = WriteLock()

    private val file: File by lazy(produceFile)
    private val serializer: Serializer<T> by lazy(produceSerializer)
    override val defaultData: T by lazy(produceDefaultData)

    override val data: Flow<T>
        get() = changeNotifier
            .onStart { emit(getData()) }
            .distinctUntilChanged()

    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        return withContext(scope.coroutineContext) {
            val currentData = getData()
            val newData = transform(currentData)
            if (newData == currentData) return@withContext newData
            writeData(newData)
            newData
        }
    }

    private suspend fun getData(): T = withContext(scope.coroutineContext) {
        writeLock.awaitWrite()
        val cached = cachedDataMutex.withLock { cachedData }
        if (cached !== this@DiskDataStoreImpl) return@withContext cached as T

        if (!file.exists()) return@withContext defaultData

        val serializedData = try {
            file.bufferedReader().use {
                it.readText()
            }
        } catch (t: Throwable) {
            throw IOException("Couldn't read file at '$file'", t)
        }

        val deserializedData = try {
            serializer.deserialize(serializedData)
        } catch (t: Throwable) {
            val newData = corruptionHandler.onCorruption(this@DiskDataStoreImpl, serializedData, t)

            try {
                writeData(newData)
            } catch (writeEx: IOException) {
                // If we fail to write the handled data, add the new exception as a suppressed
                // exception.
                t.addSuppressed(writeEx)
                throw t
            }

            // If we reach this point, we've successfully replaced the data on disk with newData.
            newData
        }

        cachedDataMutex.withLock { cachedData = deserializedData }

        return@withContext deserializedData
    }

    private suspend fun writeData(newData: T) = withContext(scope.coroutineContext) {
        try {
            writeLock.beginWrite()

            if (!file.exists()) {
                file.parentFile?.mkdirs()

                if (!file.createNewFile()) {
                    throw IOException("Couldn't create '$file'")
                }
            }

            val serializedData = try {
                serializer.serialize(newData)
            } catch (t: Throwable) {
                throw RuntimeException(
                    "Couldn't serialize data '$newData' for file '$file'",
                    t
                )
            }

            val tmpFile = File.createTempFile(
                "new", "tmp", file.parentFile
            )

            try {
                tmpFile.bufferedWriter().use { it.write(serializedData) }
                if (!tmpFile.renameTo(file)) {
                    throw IOException("$tmpFile could not be renamed to $file")
                }
            } catch (e: IOException) {
                if (tmpFile.exists()) {
                    tmpFile.delete()
                }
                throw e
            } finally {
                tmpFile.delete()
            }

            cachedDataMutex.withLock { cachedData = newData }
            changeNotifier.emit(newData)
        } finally {
            writeLock.endWrite()
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
