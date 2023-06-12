/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalSerializationApi::class)

package com.ivianuu.essentials.android.prefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.InitialOrDefault
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.IOCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

class DataStoreModule<T : Any>(private val name: String, private val default: () -> T) {
  @Provide fun dataStore(
    coroutineContext: IOCoroutineContext,
    initial: () -> @Initial T = default,
    json: Json,
    serializerFactory: () -> KSerializer<T>,
    prefsDir: () -> PrefsDir,
    scope: ScopedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<T> {
    val androidDataStore = DataStoreFactory.create(
      object : Serializer<T> {
        override val defaultValue: T
          get() = initial()

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
      corruptionHandler = ReplaceFileCorruptionHandler {
        it.printStackTrace()
        initial()
      },
      produceFile = { prefsDir().resolve(name) },
      scope = scope.childCoroutineScope(coroutineContext)
    )

    return object : DataStore<T> {
      override val data: Flow<T>
        get() = androidDataStore.data

      override suspend fun updateData(transform: T.() -> T): T =
        androidDataStore.updateData { transform(it) }
    }
  }

  @Provide
  fun initialOrDefault(initial: () -> @Initial T = default): @InitialOrDefault T = initial()
}
