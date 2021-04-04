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

package com.ivianuu.essentials.android.prefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.awaitAsync
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.InitialOrFallback
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class PrefModule<T : Any>(private val name: String) {
    @Given
    fun prefDataStore(
        @Given scope: ScopeCoroutineScope<AppGivenScope>,
        @Given dispatcher: IODispatcher,
        @Given initialFactory: () -> @InitialOrFallback T,
        @Given jsonFactory: () -> Json,
        @Given serializerFactory: () -> KSerializer<T>,
        @Given prefsDir: () -> PrefsDir
    ): @Scoped<AppGivenScope> DataStore<T> {
        val deferredDataStore: DataStore<T> by lazy {
            DataStoreFactory.create(
                produceFile = { prefsDir().resolve(name) },
                serializer = object : Serializer<T> {
                    override val defaultValue: T
                        get() = initialFactory()
                    private val json by lazy(jsonFactory)
                    private val serializer by lazy(serializerFactory)
                    override suspend fun readFrom(input: InputStream): T = runCatching {
                        json.decodeFromString(serializer, String(input.readBytes()))
                    }.fold(
                        success = { it },
                        failure = { throw CorruptionException("Couldn't deserialize data", it) }
                    )
                    override suspend fun writeTo(t: T, output: OutputStream) {
                        output.write(json.encodeToString(serializer, t).toByteArray())
                    }
                },
                scope = scope.childCoroutineScope(dispatcher),
                corruptionHandler = ReplaceFileCorruptionHandler {
                    it.printStackTrace()
                    initialFactory()
                }
            )
        }
        return object : DataStore<T> {
            override val data: Flow<T>
                get() = deferredDataStore.data

            override suspend fun updateData(transform: suspend (t: T) -> T): T = scope.awaitAsync {
                deferredDataStore.updateData(transform)
            }
        }
    }

    @Given
    fun flow(@Given dataStore: DataStore<T>): Flow<T> = dataStore.data

    @Given
    fun prefActionCollector(
        @Given dataStore: DataStore<T>,
        @Given scope: ScopeCoroutineScope<AppGivenScope>
    ): @Scoped<AppGivenScope> Collector<PrefAction<T>> = { action ->
        when (action) {
            is PrefAction.Update -> {
                scope.launch {
                    val newValue = dataStore.updateData { action.reducer(it) }
                    action.result?.complete(newValue)
                }
            }
        }
    }
}

sealed class PrefAction<T : Any> {
    data class Update<T : Any>(
        val result: CompletableDeferred<T>? = null,
        val reducer: T.() -> T
    ) : PrefAction<T>()
}

suspend fun <T : Any> Collector<PrefAction<T>>.update(reducer: T.() -> T): T {
    val result = CompletableDeferred<T>()
    this(PrefAction.Update(result, reducer))
    return result.await()
}

fun <T : Any> Collector<PrefAction<T>>.dispatchUpdate(reducer: T.() -> T) {
    this(PrefAction.Update(reducer = reducer))
}
