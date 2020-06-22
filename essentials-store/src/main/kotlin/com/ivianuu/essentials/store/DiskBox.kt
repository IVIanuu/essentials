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

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.completeWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

interface Serializer<T> {
    fun deserialize(serialized: String): T
    fun serialize(data: T): String
}

class SerializerException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)

fun <T> DiskBox(
    produceFile: () -> File,
    produceSerializer: () -> Serializer<T>,
    produceDefaultData: () -> T,
    scope: CoroutineScope
): Box<T> = DiskBoxImpl(
    produceFile = produceFile,
    produceDefaultData = produceDefaultData,
    produceSerializer = produceSerializer,
    scope = scope
)

internal class DiskBoxImpl<T>(
    produceFile: () -> File,
    produceSerializer: () -> Serializer<T>,
    produceDefaultData: () -> T,
    private val scope: CoroutineScope
) : Box<T> {

    override val data: Flow<T> = flow {
        val curChannel = downstreamChannel.get()
        actor.offer(Message.Read(curChannel))
        emitAll(curChannel.asFlow())
    }

    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        return withContext(scope.coroutineContext) {
            val ack = CompletableDeferred<T>()
            val dataChannel = downstreamChannel.get()
            val updateMsg = Message.Update<T>(transform, ack, dataChannel)

            actor.send(updateMsg)

            if (dataChannel.valueOrNull == null) {
                dataChannel.asFlow().first()
            }

            ack.await()
        }
    }

    private val file: File by lazy(produceFile)
    private val serializer: Serializer<T> by lazy(produceSerializer)
    override val defaultData: T by lazy(produceDefaultData)

    private val downstreamChannel: AtomicReference<ConflatedBroadcastChannel<T>> =
        AtomicReference(ConflatedBroadcastChannel())

    private sealed class Message<T> {
        abstract val dataChannel: ConflatedBroadcastChannel<T>

        class Read<T>(
            override val dataChannel: ConflatedBroadcastChannel<T>
        ) : Message<T>()

        class Update<T>(
            val transform: suspend (t: T) -> T,
            val ack: CompletableDeferred<T>,
            override val dataChannel: ConflatedBroadcastChannel<T>
        ) : Message<T>()
    }

    private val actor: SendChannel<Message<T>> = scope.actor(
        capacity = Channel.UNLIMITED
    ) {
        try {
            messageConsumer@ for (msg in channel) {
                if (msg.dataChannel.isClosedForSend) continue@messageConsumer

                try {
                    readAndInitOnce(msg.dataChannel)
                } catch (ex: Throwable) {
                    resetDataChannel(ex)
                    continue@messageConsumer
                }

                if (msg is Message.Update) {
                    msg.ack.completeWith(
                        runCatching {
                            transformAndWrite(msg.transform, downstreamChannel.get())
                        }
                    )
                }
            }
        } finally {
            downstreamChannel.get().cancel()
        }
    }

    private fun resetDataChannel(ex: Throwable) {
        val failedDataChannel = downstreamChannel.getAndSet(ConflatedBroadcastChannel())
        failedDataChannel.close(ex)
    }

    private fun readAndInitOnce(dataChannel: ConflatedBroadcastChannel<T>) {
        if (dataChannel.valueOrNull != null) return

        val data = if (!file.exists()) {
            defaultData
        } else {
            val serializedData = try {
                file.bufferedReader().use {
                    it.readText()
                }
            } catch (e: Exception) {
                throw IOException("Couldn't read file '$file'", e)
            }

            try {
                serializer.deserialize(serializedData)
            } catch (e: Exception) {
                throw SerializerException(
                    "Couldn't deserialize '$serializedData' for file '$file'",
                    e
                )
            }
        }

        dataChannel.offer(data)
    }

    private suspend fun transformAndWrite(
        transform: suspend (t: T) -> T,
        updateDataChannel: ConflatedBroadcastChannel<T>
    ): T {
        val curData = updateDataChannel.value
        val newData = transform(curData)
        return if (curData == newData) {
            curData
        } else {
            writeData(newData)
            updateDataChannel.offer(newData)
            newData
        }
    }

    private fun writeData(newData: T) {
        if (!file.exists()) {
            file.parentFile?.mkdirs()

            if (!file.createNewFile()) {
                throw IOException("Couldn't create '$file'")
            }
        }


        val serializedData = try {
            serializer.serialize(newData)
        } catch (e: Exception) {
            throw SerializerException(
                "Couldn't serialize data '$newData' for file '$file'",
                e
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
        } catch (ex: IOException) {
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            throw ex
        } finally {
            tmpFile.delete()
        }
    }

}
