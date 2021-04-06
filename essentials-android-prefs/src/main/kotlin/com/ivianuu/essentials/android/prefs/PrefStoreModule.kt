package com.ivianuu.essentials.android.prefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.data.ValueAction
import com.ivianuu.essentials.data.ValueAction.Update
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.InitialOrFallback
import com.ivianuu.essentials.store.effectOn
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class PrefStoreModule<T : Any>(private val name: String) {
    @Given
    fun store(
        @Given dispatcher: IODispatcher,
        @Given initialFactory: () -> @InitialOrFallback T,
        @Given jsonFactory: () -> Json,
        @Given serializerFactory: () -> KSerializer<T>,
        @Given prefsDir: () -> PrefsDir
    ): StoreBuilder<AppGivenScope, T, ValueAction<T>> = {
        val dataStore =  DataStoreFactory.create(
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
            scope = childCoroutineScope(dispatcher),
            corruptionHandler = ReplaceFileCorruptionHandler {
                it.printStackTrace()
                initialFactory()
            }
        )
        dataStore.data.update { it }
        effectOn<Update<T>> { action ->
            action.result.complete(dataStore.updateData { action.transform(it) })
        }
    }
}
