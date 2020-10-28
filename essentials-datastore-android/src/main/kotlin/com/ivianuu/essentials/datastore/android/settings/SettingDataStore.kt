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

package com.ivianuu.essentials.datastore.android.settings

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.essentials.datastore.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

object SettingDataStore {
    enum class Type {
        Global, Secure, System
    }

    interface Adapter<T> {
        fun get(
            name: String,
            defaultData: T,
            contentResolver: ContentResolver,
            type: Type
        ): T

        fun set(
            name: String,
            data: T,
            contentResolver: ContentResolver,
            type: Type
        )
    }
}

private val MainHandler = Handler()

internal class SettingDataStoreImpl<T>(
    private val type: SettingDataStore.Type,
    val name: String,
    override val defaultData: T,
    private val adapter: SettingDataStore.Adapter<T>,
    private val contentResolver: ContentResolver,
    private val mainDispatcher: MainDispatcher,
    private val scope: CoroutineScope,
) : DataStore<T> {

    private val uri: Uri by lazy {
        when (type) {
            SettingDataStore.Type.Global -> Settings.Global.getUriFor(name)
            SettingDataStore.Type.Secure -> Settings.Secure.getUriFor(name)
            SettingDataStore.Type.System -> Settings.System.getUriFor(name)
        }
    }

    override val data: Flow<T> = callbackFlow<Unit> {
        val observer = withContext(mainDispatcher) {
            object : ContentObserver(MainHandler) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    offerSafe(Unit)
                }
            }
        }
        contentResolver.registerContentObserver(uri, false, observer)
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }
        .flowOn(mainDispatcher)
        .onStart { emit(Unit) }
        .map { get() }
        .distinctUntilChanged()
        .shareIn(
            scope = scope,
            replay = 1,
            started = SharingStarted.WhileSubscribed()
        )

    override suspend fun updateData(transform: suspend (T) -> T): T =
        withContext(scope.coroutineContext) {
            try {
                val currentData = get()
                val newData = transform(currentData)
                if (currentData == newData) currentData
                else {
                    adapter.set(name, newData, contentResolver, type)
                    newData
                }
            } catch (t: Throwable) {
                throw RuntimeException("Couldn't write data for name: $name", t)
            }
        }

    private suspend fun get(): T = withContext(scope.coroutineContext) {
        try {
            adapter.get(name, defaultData, contentResolver, type)
        } catch (t: Throwable) {
            throw RuntimeException("Couldn't read data for name: $name", t)
        }
    }
}
