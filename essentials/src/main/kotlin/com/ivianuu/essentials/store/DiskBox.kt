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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode.READ_ONLY
import java.nio.channels.FileChannel.MapMode.READ_WRITE
import java.nio.channels.FileLock
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

interface DiskBox<T> : Box<T> {
    val file: File

    interface Serializer<T> {
        suspend fun deserialize(serialized: String): T
        suspend fun serialize(value: T): String
    }
}

fun <T> DiskBox(
    context: Context,
    file: File,
    serializer: DiskBox.Serializer<T>,
    defaultValue: suspend () -> T,
    dispatcher: CoroutineDispatcher? = null
): DiskBox<T> = DiskBoxImpl(
    context = context,
    file = file,
    serializer = serializer,
    defaultValueLambda = defaultValue,
    dispatcher = dispatcher
)

internal class DiskBoxImpl<T>(
    context: Context,
    override val file: File,
    defaultValueLambda: suspend () -> T,
    private val serializer: DiskBox.Serializer<T>,
    private val dispatcher: CoroutineDispatcher? = null
) : DiskBox<T> {

    private val changeNotifier = BroadcastChannel<Unit>(1)
    private val cachedValue = AtomicReference<Any?>(this)
    private val lazyDefaultValue = SuspendLazy(defaultValueLambda)
    private val valueFetcher = MutexValue {
        var lock: FileLock? = null
        var channel: FileChannel? = null
        var randomAccessFile: RandomAccessFile? = null

        try {
            randomAccessFile = RandomAccessFile(file, "rw")

            channel = randomAccessFile.channel
            lock = channel.lock()

            val size = randomAccessFile.length().toInt()
            val buffer = channel!!.map(READ_ONLY, 0, size.toLong())

            val bytes = ByteArray(size)
            buffer.get(bytes)
            val serialized = bytes.decodeToString()
            return@MutexValue serializer.deserialize(serialized)
        } catch (e: Exception) {
            throw IOException("Couldn't read file $file")
        } finally {
            try {
                randomAccessFile?.close()
                channel?.close()
                lock?.release()
            } catch (ignored: Exception) {
            }
        }
    }
    private val multiProcessHelper = MultiProcessHelper(context, file) {
        cachedValue.set(this) // force refetching the value
        changeNotifier.offer(Unit)
    }
    private val disposed = AtomicBoolean(false)

    init {
        check(!file.isDirectory)
    }

    override suspend fun defaultValue(): T {
        checkNotDisposed()
        return lazyDefaultValue()
    }

    override suspend fun set(value: T) {
        checkNotDisposed()

        maybeWithDispatcher {
            val serialized = serializer.serialize(value)
            val bytes = serialized.toByteArray()

            var lock: FileLock? = null
            var channel: FileChannel? = null
            var randomAccessFile: RandomAccessFile? = null

            try {
                randomAccessFile = RandomAccessFile(file, "rw")
                randomAccessFile.setLength(0)

                channel = randomAccessFile.channel
                lock = channel.lock()

                val byteBuffer = channel.map(READ_WRITE, 0, bytes.size.toLong())

                byteBuffer.put(bytes)
                channel.write(byteBuffer)
                byteBuffer.force()
            } catch (e: Exception) {
                throw IOException("Couldn't write to file $file $serialized", e)
            } finally {
                try {
                    randomAccessFile?.close()
                    channel?.close()
                    lock?.release()
                } catch (ignored: Exception) {
                }
            }

            cachedValue.set(value)
            changeNotifier.offer(Unit)
            multiProcessHelper.notifyWrite()
        }
    }

    override suspend fun get(): T {
        checkNotDisposed()
        return maybeWithDispatcher {
            val cached = cachedValue.get()
            if (cached != this) return@maybeWithDispatcher cached as T

            val value = if (isSet()) valueFetcher() else defaultValue()
            cachedValue.set(value)
            return@maybeWithDispatcher value
        }
    }

    override suspend fun delete() {
        checkNotDisposed()
        maybeWithDispatcher {
            if (file.exists()) {
                if (!file.delete()) {
                    throw IOException("Couldn't delete file $file")
                }
            }

            cachedValue.set(this)
            changeNotifier.offer(Unit)
        }
    }

    override suspend fun isSet(): Boolean {
        checkNotDisposed()
        return maybeWithDispatcher {
            file.exists()
        }
    }

    override fun asFlow(): Flow<T> {
        checkNotDisposed()
        return flow {
            emit(get())
            changeNotifier.asFlow().collect { emit(get()) }
        }
    }

    override fun dispose() {
        if (disposed.getAndSet(true)) {
            multiProcessHelper.dispose()
        }
    }

    private suspend fun <T> maybeWithDispatcher(block: suspend () -> T): T =
        if (dispatcher != null) withContext(dispatcher) { block() } else block()

    private fun checkNotDisposed() {
        require(disposed.get()) { "Box is already disposed" }
    }
}

private class MultiProcessHelper(
    private val context: Context,
    private val file: File,
    private val onChange: () -> Unit
) {

    private val uuid = UUID.randomUUID().toString()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra(EXTRA_CHANGE_OWNER) != uuid
                && file.absolutePath == intent.getStringExtra(EXTRA_PATH)
            ) {
                onChange()
            }
        }
    }

    init {
        try {
            context.registerReceiver(receiver, IntentFilter().apply {
                addAction(ACTION_ON_CHANGE)
            })
        } catch (e: Exception) {
        }
    }

    fun notifyWrite() {
        try {
            context.sendBroadcast(Intent(ACTION_ON_CHANGE).apply {
                putExtra(EXTRA_CHANGE_OWNER, uuid)
                putExtra(EXTRA_PATH, file.absolutePath)
            })
        } catch (e: Exception) {
        }
    }

    fun dispose() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
        }
    }

    private companion object {
        private const val ACTION_ON_CHANGE = "com.ivianuu.essentials.store.ON_CHANGE"
        private const val EXTRA_CHANGE_OWNER = "change_owner"
        private const val EXTRA_PATH = "path"
    }
}

private class SuspendLazy<T>(private val init: suspend () -> T) {

    private val lazyValue = AtomicReference<Any?>(this)
    private val _mutexValue = MutexValue {
        val cached = lazyValue.get()
        if (cached != this) return@MutexValue cached as T
        val value = init()
        lazyValue.set(value)
        return@MutexValue value
    }

    suspend operator fun invoke(): T = _mutexValue()

}

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