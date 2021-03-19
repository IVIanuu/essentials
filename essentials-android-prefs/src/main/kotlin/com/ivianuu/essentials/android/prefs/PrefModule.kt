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

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.awaitAsync
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.scope.Scoped
import com.ivianuu.injekt.scope.AppGivenScope
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

class PrefModule<T : Any>(private val name: String) {
    @Given
    fun prefDataStore(
        @Given scope: ScopeCoroutineScope<AppGivenScope>,
        @Given dispatcher: IODispatcher,
        @Given initialFactory: () -> @InitialOrFallback T,
        @Given adapterFactory: () -> JsonAdapter<T>,
        @Given prefsDir: () -> PrefsDir
    ): @Scoped<AppGivenScope> DataStore<T> {
        val deferredDataStore: DataStore<T> by lazy {
            DataStoreFactory.create(
                produceFile = { prefsDir().resolve(name) },
                serializer = object : Serializer<T> {
                    override val defaultValue: T
                        get() = initialFactory()
                    private val adapter by lazy(adapterFactory)
                    override suspend fun readFrom(input: InputStream): T =
                        adapter.fromJson(String(input.readBytes()))!!

                    override suspend fun writeTo(t: T, output: OutputStream) {
                        output.write(adapter.toJson(t)!!.toByteArray())
                    }
                },
                scope = scope.childCoroutineScope(dispatcher)
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

    @Scoped<AppGivenScope>
    @Given
    fun stateFlow(
        @Given dataStore: DataStore<T>,
        @Given scope: ScopeCoroutineScope<AppGivenScope>,
        @Given initial: @InitialOrFallback T
    ): StateFlow<T> = dataStore.data.stateIn(scope, SharingStarted.Eagerly, initial)

    @Scoped<AppGivenScope>
    @Given
    fun prefActionCollector(
        @Given dataStore: DataStore<T>,
        @Given scope: ScopeCoroutineScope<AppGivenScope>
    ): Collector<PrefAction<T>> = { action ->
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

@Qualifier
internal annotation class InitialOrFallback

@Given
inline fun <reified T : Any> initialOrFallback(
    @Given initial: @Initial T? = null
): @InitialOrFallback T = initial ?: T::class.java.newInstance()

