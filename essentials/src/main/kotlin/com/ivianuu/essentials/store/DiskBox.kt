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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicReference

interface DiskBox<T> : Box<T> {
    val file: File

    interface Serializer<T> {
        fun deserialize(serialized: String): T
        fun serialize(value: T): String
    }
}

fun <T> DiskBox(
    file: File,
    serializer: DiskBox.Serializer<T>,
    defaultValue: T,
    dispatcher: CoroutineDispatcher? = null
): DiskBox<T> = DiskBoxImpl(
    file = file,
    serializer = serializer,
    defaultValue = defaultValue,
    dispatcher = dispatcher
)

internal class DiskBoxImpl<T>(
    override val defaultValue: T,
    override val file: File,
    private val serializer: DiskBox.Serializer<T>,
    private val dispatcher: CoroutineDispatcher? = null
) : DiskBox<T> {

    private val channel = BroadcastChannel<T>(1)
    private val cachedValue = AtomicReference(defaultValue)

    init {
        check(!file.isDirectory)
    }

    override suspend fun set(value: T): Unit = maybeWithDispatcher {
        cachedValue.set(value)

        val serialized = serializer.serialize(value)

        val tmpFile = File.createTempFile(
            "new", "tmp", file.parentFile
        )
        try {
            BufferedOutputStream(FileOutputStream(tmpFile)).run {
                write(serialized.toByteArray())
                flush()
                close()
            }
            if (!tmpFile.renameTo(file)) {
                throw IOException("Couldn't move tmp file to file $file")
            }
        } catch (e: Exception) {
            throw IOException("Couldn't write to file $file $serialized", e)
        }

        channel.offer(value)
    }

    private val _mutexValue = MutexValue {
        val cached = cachedValue.get()
        if (cached != this) return@MutexValue cached as T

        try {
            val reader = BufferedReader(InputStreamReader(FileInputStream(file)))
            val stringBuilder = StringBuilder()
            var line = reader.readLine()
            while (line != null) {
                stringBuilder.append(line).append('\n')
                line = reader.readLine()
            }

            reader.close()

            val value = serializer.deserialize(stringBuilder.toString())
            cachedValue.set(value)
            value
        } catch (e: Exception) {
            throw IOException("Couldn't read file $file")
        }
    }

    override suspend fun get(): T = maybeWithDispatcher { _mutexValue.get() }

    override suspend fun delete() = maybeWithDispatcher {
        cachedValue.set(defaultValue)
        if (file.exists()) {
            if (!file.delete()) {
                throw IOException("Couldn't delete file $file")
            }
        }
    }

    override suspend fun isSet(): Boolean = maybeWithDispatcher {
        file.exists()
    }

    override fun asFlow(): Flow<T> = flow {
        emit(get())
        channel.asFlow().collect { emit(get()) }
    }

    private suspend fun <T> maybeWithDispatcher(block: suspend () -> T): T =
        if (dispatcher != null) withContext(dispatcher) { block() } else block()
}

private class MutexValue<T>(private val getter: () -> T) {

    private var currentDeferred: Deferred<T>? = null
    private val deferredLock = Any()

    suspend fun get(): T {
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