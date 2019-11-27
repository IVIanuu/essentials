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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileLock
import java.util.*
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
    private val context: Context,
    override val file: File,
    defaultValueLambda: suspend () -> T,
    private val serializer: DiskBox.Serializer<T>,
    private val dispatcher: CoroutineDispatcher? = null
) : DiskBox<T> {

    private val changeNotifier = BroadcastChannel<Unit>(1)
    private val cachedValue = AtomicReference<Any?>(this)
    private val lazyDefaultValue = SuspendLazy(defaultValueLambda)
    private val valueFetcher = MutexValue {
        var inputStream: FileInputStream? = null
        var lock: FileLock? = null
        try {
            inputStream = FileInputStream(file)
            lock = inputStream.channel.tryLock(0L, Long.MAX_VALUE, true)
            val value = inputStream.bufferedReader().readText()
            return@MutexValue serializer.deserialize(value)
        } catch (e: Exception) {
            throw IOException("Couldn't read file $file")
        } finally {
            inputStream?.close()
            lock?.release()
        }
    }
    private val multiProcessHelper = MultiProcessHelper(context, file) {
        changeNotifier.offer(Unit)
    }

    init {
        check(!file.isDirectory)
    }

    override suspend fun defaultValue(): T = lazyDefaultValue.invoke()

    override suspend fun set(value: T): Unit = maybeWithDispatcher {
        val serialized = serializer.serialize(value)

        val tmpFile = File.createTempFile(
            "new", "tmp", file.parentFile
        )

        var outputStream: FileOutputStream? = null
        var lock: FileLock? = null
        try {
            outputStream = FileOutputStream(file)
            lock = outputStream.channel.tryLock(0L, Long.MAX_VALUE, true)
            outputStream.bufferedWriter().write(serialized)
            if (!tmpFile.renameTo(file)) {
                throw IOException("Couldn't move tmp file to file $file")
            }
        } catch (e: Exception) {
            throw IOException("Couldn't write to file $file $serialized", e)
        } finally {
            outputStream?.flush()
            outputStream?.close()
            lock?.release()
        }

        cachedValue.set(value)
        changeNotifier.offer(Unit)
        multiProcessHelper.notifyWrite()
    }

    override suspend fun get(): T = maybeWithDispatcher {
        val cached = cachedValue.get()
        if (cached != this) return@maybeWithDispatcher cached as T

        val value = if (isSet()) valueFetcher() else defaultValue()
        cachedValue.set(value)
        return@maybeWithDispatcher value
    }

    override suspend fun delete(): Unit = maybeWithDispatcher {
        if (file.exists()) {
            if (!file.delete()) {
                throw IOException("Couldn't delete file $file")
            }
        }

        cachedValue.set(this)
        changeNotifier.offer(Unit)
    }

    override suspend fun isSet(): Boolean = maybeWithDispatcher {
        file.exists()
    }

    override fun asFlow(): Flow<T> = flow {
        emit(get())
        changeNotifier.asFlow().collect { emit(get()) }
    }

    private suspend fun <T> maybeWithDispatcher(block: suspend () -> T): T =
        if (dispatcher != null) withContext(dispatcher) { block() } else block()
}

private class MultiProcessHelper(
    private val context: Context,
    private val file: File,
    private val onChange: () -> Unit
) {

    private val uuid = UUID.randomUUID().toString()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (file.absolutePath == intent.getStringExtra(EXTRA_PATH)
                && intent.getStringExtra(EXTRA_CHANGE_OWNER) != uuid
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
                putExtra(EXTRA_PATH, file.absoluteFile)
            })
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