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
import com.ivianuu.essentials.data.StoreAction
import com.ivianuu.essentials.data.StoreAction.Update
import com.ivianuu.essentials.store.FeatureBuilder
import com.ivianuu.essentials.store.InitialOrFallback
import com.ivianuu.essentials.store.actionsOf
import com.ivianuu.essentials.store.collectIn
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class PrefFeatureModule<T : Any>(private val name: String) {
    @Given
    fun feature(
        @Given dispatcher: IODispatcher,
        @Given initialFactory: () -> @InitialOrFallback T,
        @Given jsonFactory: () -> Json,
        @Given serializerFactory: () -> KSerializer<T>,
        @Given prefsDir: () -> PrefsDir
    ): FeatureBuilder<AppGivenScope, T, StoreAction<T>> = {
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
        dataStore.data
            .updateIn(this) { it }
        actionsOf<Update<T>>()
            .collectIn(this) { update ->
                update.complete(dataStore.updateData { update.transform(it) })
            }
    }
}
