/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.data

import androidx.datastore.core.*
import androidx.datastore.core.Serializer
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.*

class DataStoreProvider<T : Any>(
  private val name: String,
  private val default: () -> T
) {
  @Provide fun provide(
    coroutineContexts: CoroutineContexts,
    json: () -> Json,
    serializerFactory: () -> KSerializer<T>,
    dirs: () -> AppDirs,
    scope: @For<AppScope> CoroutineScope
  ): @Scoped<AppScope> DataStore<T> = DataStoreFactory.create(
    object : Serializer<T> {
      override val defaultValue: T get() = default()

      private val serializer by lazy(serializerFactory)

      override suspend fun readFrom(input: InputStream): T =
        try {
          json().decodeFromStream(serializer, input)
        } catch (e: SerializationException) {
          throw CorruptionException("Could not read ${String(input.readBytes())}", e)
        }

      override suspend fun writeTo(t: T, output: OutputStream) {
        try {
          json().encodeToStream(serializer, t, output)
        } catch (e: SerializationException) {
          throw CorruptionException("Could not write $t", e)
        }
      }
    },
    produceFile = { dirs().prefs.resolve(name) },
    scope = scope + coroutineContexts.io
  )
}
