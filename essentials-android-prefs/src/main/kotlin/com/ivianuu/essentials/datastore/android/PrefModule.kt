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

package com.ivianuu.essentials.datastore.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

class PrefModule<T : Any>(private val name: String) {
    @Given operator fun invoke(
        @Given scope: GlobalScope,
        @Given dispatcher: IODispatcher,
        @Given initialFactory: () -> @InitialOrFallback T,
        @Given adapterFactory: () -> JsonAdapter<T>,
        @Given prefsDir: () -> PrefsDir
    ): @Scoped<AppComponent> DataStore<T> {
        val deferredDataStore: DataStore<T> by lazy {
            DataStoreFactory.create(
                produceFile = { prefsDir().resolve(name) },
                serializer = object : Serializer<T> {
                    override val defaultValue: T
                        get() = initialFactory()
                    private val adapter by lazy(adapterFactory)
                    override fun readFrom(input: InputStream): T =
                        adapter.fromJson(String(input.readBytes()))!!

                    override fun writeTo(t: T, output: OutputStream) {
                        output.write(adapter.toJson(t)!!.toByteArray())
                    }
                },
                scope = scope.childCoroutineScope(dispatcher)
            )
        }
        return object : DataStore<T> {
            override val data: Flow<T>
                get() = deferredDataStore.data;

            override suspend fun updateData(transform: suspend (t: T) -> T): T =
                deferredDataStore.updateData(transform)
        }
    }
}

@Given
fun <T : Any> @Given DataStore<T>.dataFlow(): Flow<T> = data

@Given @Composable fun <T : Any> @Given DataStore<T>.uiState(
    @Given initial: @InitialOrFallback T
): @UiState T = data.collectAsState(initial).value

@Qualifier internal annotation class InitialOrFallback

@Given inline fun <reified T : Any> initialOrFallback(
    @Given initial: @Initial T? = null
): @InitialOrFallback T = initial ?: T::class.java.newInstance()

typealias PrefUpdater<T> = suspend (T.() -> T) -> T

@Given
fun <T> prefUpdater(@Given pref: DataStore<T>): PrefUpdater<T> = { reducer ->
    pref.updateData { reducer(it) }
}

typealias PrefUpdateDispatcher<T> = (T.() -> T) -> Unit

@Given
fun <T> prefUpdateDispatcher(
    @Given pref: DataStore<T>,
    @Given scope: GlobalScope
): PrefUpdateDispatcher<T> = { reducer ->
    scope.launch {
        pref.updateData { reducer(it) }
    }
}
