/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.data

import androidx.datastore.core.*
import androidx.datastore.core.Serializer
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.*

class DataStoreModule<T : Any>(private val name: String, private val default: () -> T) {
  @Provide fun dataStore(
    coroutineContexts: CoroutineContexts,
    json: Json,
    serializerFactory: () -> KSerializer<T>,
    prefsDir: () -> PrefsDir,
    scope: ScopedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<T> {
    val androidDataStore = DataStoreFactory.create(
      object : Serializer<T> {
        override val defaultValue: T get() = default()

        private val serializer by lazy(serializerFactory)

        override suspend fun readFrom(input: InputStream): T =
          try {
            json.decodeFromStream(serializer, input)
          } catch (e: SerializationException) {
            throw CorruptionException("Could not read ${String(input.readBytes())}", e)
          }

        override suspend fun writeTo(t: T, output: OutputStream) {
          try {
            json.encodeToStream(serializer, t, output)
          } catch (e: SerializationException) {
            throw CorruptionException("Could not write $t", e)
          }
        }
      },
      produceFile = { prefsDir().resolve(name) },
      scope = scope.childCoroutineScope(coroutineContexts.io)
    )

    return object : DataStore<T> {
      override val data: Flow<T> = androidDataStore.data

      override suspend fun updateData(transform: T.() -> T): T =
        androidDataStore.updateData { transform(it) }
    }
  }
}
