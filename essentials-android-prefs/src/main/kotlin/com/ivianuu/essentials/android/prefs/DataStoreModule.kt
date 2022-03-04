/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.prefs

import androidx.datastore.core.*
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.*

class DataStoreModule<T : Any>(private val name: String, private val default: () -> T) {
  @Provide fun dataStore(
    context: IOContext,
    initial: () -> @Initial T = default,
    json: Json,
    serializerFactory: () -> KSerializer<T>,
    prefsDir: () -> PrefsDir,
    scope: NamedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<T> {
    val androidDataStore = DataStoreFactory.create(
      object : Serializer<T> {
        override val defaultValue: T
          get() = initial()

        private val serializer by lazy(serializerFactory)

        override suspend fun readFrom(input: InputStream): T =
          json.decodeFromStream(serializer, input)

        override suspend fun writeTo(t: T, output: OutputStream) {
          json.encodeToStream(serializer, t, output)
        }
      },
      corruptionHandler = ReplaceFileCorruptionHandler {
        it.printStackTrace()
        initial()
      },
      produceFile = { prefsDir().resolve(name) },
      scope = scope.childCoroutineScope(context)
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
