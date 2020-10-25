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

import com.ivianuu.essentials.datastore.DiskDataStoreImpl.Message.InitializeIfNeeded
import com.ivianuu.essentials.datastore.DiskDataStoreImpl.Message.UpdateData
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.completeWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
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
    scope: CoroutineScope,
    private val corruptionHandler: CorruptionHandler
) : DataStore<T> {

    private val file: File by lazy(produceFile)
    private val serializer: Serializer<T> by lazy(produceSerializer)
    override val defaultData: T by lazy(produceDefaultData)

    private val _data = MutableSharedFlow<T>(replay = 1)
    override val data: Flow<T>
        get() = _data
            .onStart { actor.offer(InitializeIfNeeded()) }
            .distinctUntilChanged()

    private val actor = scope.actor<Message<T>>(capacity = Channel.UNLIMITED) {
        for (message in channel) {
            when (message) {
                is UpdateData -> {
                    message.newData.completeWith(
                        runCatching {
                            val currentData = getAndInitializeDataOnce()
                            val newData = message.transform(currentData)
                            if (newData != currentData) {
                                writeData(newData)
                                _data.emit(newData)
                            }
                            newData
                        }
                    )
                    Unit
                }
                is InitializeIfNeeded -> {
                    if (_data.replayCache.isEmpty()) {
                        _data.emit(getAndInitializeDataOnce())
                    } else Unit
                }
            }.let {}
        }
    }

    private sealed class Message<T> {
        class UpdateData<T>(
            val transform: suspend (T) -> T,
            val newData: CompletableDeferred<T>
        ) : Message<T>()
        class InitializeIfNeeded<T> : Message<T>()
    }

    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        val deferred = CompletableDeferred<T>()
        actor.offer(UpdateData(transform, deferred))
        return deferred.await()
    }

    private suspend fun getAndInitializeDataOnce(): T {
        _data.replayCache.let { if (it.isNotEmpty()) return it.single() }

        if (!file.exists()) return defaultData

        return try {
            val serializedData = try {
                file.readText()
            } catch (readException: Throwable) {
                throw IOException("Couldn't read file at '$file'", readException)
            }

            serializer.deserialize(serializedData)
        } catch (readException: Throwable) {
            val newData = corruptionHandler.onCorruption(this@DiskDataStoreImpl, readException)

            try {
                writeData(newData)
            } catch (t2: IOException) {
                // If we fail to write the handled data, add the new exception as a suppressed
                // exception.
                readException.addSuppressed(t2)
                throw readException
            }

            // If we reach this point, we've successfully replaced the data on disk with newData.
            newData
        }
    }

    private suspend fun writeData(newData: T) {
        if (!file.exists()) {
            file.parentFile?.mkdirs()

            if (!file.createNewFile()) {
                throw IOException("Couldn't create '$file'")
            }
        }

        val serializedData = try {
            serializer.serialize(newData)
        } catch (t: Throwable) {
            throw RuntimeException("Couldn't serialize data '$newData' for file '$file'", t)
        }

        val tmpFile = File.createTempFile(
            "new", "tmp", file.parentFile
        )

        try {
            tmpFile.writeText(serializedData)
            if (!tmpFile.renameTo(file)) {
                throw IOException("$tmpFile couldn't be renamed to $file")
            }
        } catch (e: IOException) {
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            throw e
        } finally {
            tmpFile.delete()
        }
    }
}
