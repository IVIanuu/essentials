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
import androidx.datastore.createDataStore
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.injekt.Arg
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.Scoped
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.merge.ApplicationComponent
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

@Effect
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS)
annotation class PrefBinding(val name: String) {
    companion object {
        @Scoped(ApplicationComponent::class)
        @Binding
        fun <T : Any> pref(
            @Arg("name") name: String,
            scope: GlobalScope,
            initialFactory: () -> @InitialOrFallback T,
            adapterFactory: () -> JsonAdapter<T>,
            prefsDir: () -> PrefsDir,
        ): DataStore<T> {
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
                    scope = scope
                )
            }
            return object : DataStore<T> {
                override val data: Flow<T>
                    get() = deferredDataStore.data;

                override suspend fun updateData(transform: suspend (t: T) -> T): T =
                    deferredDataStore.updateData(transform)
            }
        }

        @Suppress("NOTHING_TO_INLINE")
        @Binding
        inline fun <T : Any> flow(dataStore: DataStore<T>): Flow<T> = dataStore.data

        @Scoped(ApplicationComponent::class)
        @Binding
        fun <T : Any> stateFlow(
            scope: GlobalScope,
            flow: Flow<T>,
            initial: @InitialOrFallback T,
        ): StateFlow<T> = flow.stateIn(scope, SharingStarted.Eagerly, initial)

        // todo inline once compose/kotlin is fixed
        @Binding
        @Composable
        fun <T : Any> StateFlow<T>.latest(initial: @InitialOrFallback T): @UiState T =
            collectAsState(initial).value
    }
}

@Qualifier
@Target(AnnotationTarget.TYPE)
internal annotation class InitialOrFallback
@Binding
inline fun <reified T : Any> initialOrFallback(initial: @Initial T?): @InitialOrFallback T =
    initial ?: T::class.java.newInstance()

@FunBinding
fun <T> updatePref(
    pref: DataStore<T>,
    scope: GlobalScope,
    @FunApi reducer: suspend T.() -> T
) {
    scope.launch {
        pref.updateData { reducer(it) }
    }
}
