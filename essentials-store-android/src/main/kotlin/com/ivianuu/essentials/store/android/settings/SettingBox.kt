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

package com.ivianuu.essentials.store.settings

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import com.ivianuu.essentials.coroutines.shareIn
import com.ivianuu.essentials.store.Box
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

interface SettingBox<T> : Box<T> {
    val uri: Uri
    val type: Type

    enum class Type {
        Global, Secure, System
    }

    interface Adapter<T> {

        fun get(
            name: String,
            defaultValue: T,
            contentResolver: ContentResolver,
            type: Type
        ): T

        fun set(
            name: String,
            value: T,
            contentResolver: ContentResolver,
            type: Type
        )
    }
}

private val MainHandler = Handler()

class SettingBoxImpl<T>(
    override val type: SettingBox.Type,
    val name: String,
    override val defaultValue: T,
    private val adapter: SettingBox.Adapter<T>,
    private val contentResolver: ContentResolver,
    private val dispatcher: CoroutineDispatcher?
) : SettingBox<T> {

    private val _isDisposed = AtomicBoolean(false)
    override val isDisposed: Boolean
        get() = false

    override val uri: Uri by lazy {
        when (type) {
            SettingBox.Type.Global -> Settings.Global.getUriFor(name)
            SettingBox.Type.Secure -> Settings.Secure.getUriFor(name)
            SettingBox.Type.System -> Settings.System.getUriFor(name)
        }
    }

    private val coroutineScope = CoroutineScope(Job())

    private val _value = callbackFlow<Unit> {
        val observer = withContext(Dispatchers.Main) {
            object : ContentObserver(MainHandler) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    offer(Unit)
                }
            }
        }
        contentResolver.registerContentObserver(uri, false, observer)
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }
        .onStart { emit(Unit) }
        .map { get() }
        .distinctUntilChanged()
        .shareIn(scope = coroutineScope, cacheSize = 1, tag = "SettingBox:$uri")
    override val value: Flow<T>
        get() {
            checkNotDisposed()
            return _value
        }

    override suspend fun update(value: T) {
        checkNotDisposed()
        maybeWithDispatcher {
            try {
                adapter.set(name, value, contentResolver, type)
            } catch (e: Exception) {
                throw RuntimeException("couldn't write value for name: $name", e)
            }
        }
    }

    private suspend fun get(): T {
        return maybeWithDispatcher {
            try {
                adapter.get(name, defaultValue, contentResolver, type)
            } catch (e: Exception) {
                throw RuntimeException("couldn't read value for name: $name", e)
            }
        }
    }

    override fun dispose() {
        if (!_isDisposed.getAndSet(true)) {
            coroutineScope.coroutineContext[Job]!!.cancel()
        }
    }

    private suspend fun <T> maybeWithDispatcher(block: suspend () -> T): T =
        if (dispatcher != null) withContext(dispatcher) { block() } else block()

    private fun checkNotDisposed() {
        require(!_isDisposed.get()) { "Box is already disposed" }
    }
}
