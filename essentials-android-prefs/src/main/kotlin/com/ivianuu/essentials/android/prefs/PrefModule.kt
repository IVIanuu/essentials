package com.ivianuu.essentials.android.prefs

import androidx.datastore.core.*
import androidx.datastore.core.handlers.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.store.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import java.io.*

class PrefModule<T : Any>(
    private val name: String,
    private val initial: () -> T
) {
    @Given
    fun dataStore(
        @Given dispatcher: IODispatcher,
        @Given jsonFactory: () -> Json,
        @Given serializerFactory: () -> KSerializer<T>,
        @Given prefsDir: () -> PrefsDir,
        @Given scope: ScopeCoroutineScope<AppGivenScope>
    ): @Scoped<AppGivenScope> DataStore<T> {
        val dataStore = DataStoreFactory.create(
            produceFile = { prefsDir().resolve(name) },
            serializer = object : Serializer<T> {
                override val defaultValue: T
                    get() = initial()
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
                initial()
            }
        )
        val actor = scope.actor()
        return object : DataStore<T> {
            override val data: Flow<T>
                get() = dataStore.data
            override suspend fun updateData(transform: T.() -> T) = actor.actAndReply {
                dataStore.updateData { transform(it) }
                data.first()
            }
        }
    }
}
