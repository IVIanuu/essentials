package com.ivianuu.essentials.android.prefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.actAndReply
import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.store.InitialOrFallback
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class PrefModule<T : Any>(private val name: String) {
    @Given
    fun store(
        @Given dispatcher: IODispatcher,
        @Given initialFactory: () -> @InitialOrFallback T,
        @Given jsonFactory: () -> Json,
        @Given serializerFactory: () -> KSerializer<T>,
        @Given prefsDir: () -> PrefsDir,
        @Given scope: ScopeCoroutineScope<AppGivenScope>
    ): @Scoped<AppGivenScope> DataStore<T> {
        val dataStore = DataStoreFactory.create(
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
        val actor = scope.actor()
        return object : DataStore<T> {
            override val data: Flow<T>
                get() = dataStore.data
            override suspend fun updateData(transform: T.() -> T): T = actor.actAndReply {
                dataStore.updateData { transform(it) }
            }
        }
    }
}
