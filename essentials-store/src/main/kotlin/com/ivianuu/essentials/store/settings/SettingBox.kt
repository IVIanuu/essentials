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
import com.ivianuu.essentials.store.Box
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

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

    override suspend fun set(value: T) {
        checkNotDisposed()
        maybeWithDispatcher {
            try {
                adapter.set(name, value, contentResolver, type)
            } catch (e: Exception) {
                throw RuntimeException("couldn't write value for name: $name", e)
            }
        }
    }

    override suspend fun get(): T {
        checkNotDisposed()
        return maybeWithDispatcher {
            try {
                adapter.get(name, defaultValue, contentResolver, type)
            } catch (e: Exception) {
                throw RuntimeException("couldn't read value for name: $name", e)
            }
        }
    }

    override suspend fun delete() {
        checkNotDisposed()
        maybeWithDispatcher {
            adapter.set(name, defaultValue, contentResolver, type)
        }
    }

    override suspend fun isSet(): Boolean = true

    override fun asFlow(): Flow<T> = callbackFlow<Unit> {
        val observer = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                offer(Unit)
            }
        }
        contentResolver.registerContentObserver(uri, false, observer)
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }
        .onStart { emit(Unit) }
        .map { get() }

    override fun dispose() {
        _isDisposed.set(true)
    }

    private suspend fun <T> maybeWithDispatcher(block: suspend () -> T): T =
        if (dispatcher != null) withContext(dispatcher) { block() } else block()

    private fun checkNotDisposed() {
        require(!_isDisposed.get()) { "Box is already disposed" }
    }
}
