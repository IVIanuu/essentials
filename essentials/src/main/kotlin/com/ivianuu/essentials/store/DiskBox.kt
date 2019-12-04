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
import com.github.ajalt.timberkt.d
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
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.measureTimedValue

interface DiskBox<T> : Box<T> {
    val file: File

    interface Serializer<T> {
        fun deserialize(serialized: String): T
        fun serialize(value: T): String
    }
}

fun <T> DiskBox(
    context: Context,
    file: File,
    serializer: DiskBox.Serializer<T>,
    defaultValue: T,
    dispatcher: CoroutineDispatcher? = null
): DiskBox<T> = DiskBoxImpl(
    context = context,
    file = file,
    serializer = serializer,
    defaultValue = defaultValue,
    dispatcher = dispatcher
)

internal class DiskBoxImpl<T>(
    context: Context,
    override val file: File,
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
            throw IOException("Couldn't read file $file")
        }
    }
    private val multiProcessHelper = MultiProcessHelper(context, file) {
        cachedValue.set(this) // force refetching the value
        changeNotifier.offer(Unit)
    }

    init {
        check(!file.isDirectory)
        d { "$file -> init" }
    }

    override suspend fun set(value: T) {
        checkNotDisposed()
        d { "$file -> set $value" }
        maybeWithDispatcher {
            measured("set") {
                if (!file.exists()) {
                    file.parentFile.mkdirs()

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
                changeNotifier.offer(Unit)
                multiProcessHelper.notifyWrite()
            }
        }
    }

    override suspend fun get(): T {
        checkNotDisposed()
        d { "$file -> get" }
        return maybeWithDispatcher {
            measured("get") {
                val cached = cachedValue.get()
                if (cached != this) {
                    d { "$file -> return cached $cached" }
                    return@measured cached as T
                }

                return@measured if (isSet()) {
                    valueFetcher().also {
                        cachedValue.set(it)
                        d { "$file -> return fetched $it" }
                    }
                } else {
                    defaultValue.also {
                        d { "$file -> return default value $it" }
                    }
                }
            }
        }
    }

    override suspend fun delete() {
        checkNotDisposed()
        d { "$file -> delete" }
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
                    d { "$file -> exists $it" }
                }
            }
        }
    }

    override fun asFlow(): Flow<T> {
        checkNotDisposed()
        return flow {
            emit(get())
            changeNotifier.asFlow().collect {
                emit(get())
            }
        }
    }

    override fun dispose() {
        d { "$file -> dispose" }
        if (_isDisposed.getAndSet(true)) {
            multiProcessHelper.dispose()
        }
    }

    private suspend fun <T> maybeWithDispatcher(block: suspend () -> T): T =
        if (dispatcher != null) withContext(dispatcher) { block() } else block()

    private fun checkNotDisposed() {
        require(!_isDisposed.get()) { "Box is already disposed" }
    }

    private inline fun <T> measured(tag: String, block: () -> T): T {
        val (result, duration) = measureTimedValue(block)
        d { "$file -> compute '$tag' with result '$result' took ${duration.toLongMilliseconds()} ms" }
        return result
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
            if (intent.getStringExtra(EXTRA_CHANGE_OWNER) != uuid &&
                file.absolutePath == intent.getStringExtra(EXTRA_PATH)
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
