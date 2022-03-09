/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*

class PrefModule<T : Any>(private val default: () -> T) {
  @Provide fun dataStore(
    prefsDataStore: DataStore<Map<String, String?>>,
    jsonFactory: () -> Json,
    initial: () -> @Initial T = default,
    serializerFactory: () -> KSerializer<T>,
    scope: NamedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<T> {
    val json by lazy(jsonFactory)
    val serializer by lazy(serializerFactory)

    fun Map<String, String?>.decode(): T =
      if (serializer.descriptor.elementNames.any { it in this })
        PrefsDecoder(this, EmptySerializersModule, serializer.descriptor, json)
          .decodeSerializableValue(serializer) else initial()

    return object : DataStore<T> {
      override val data: Flow<T> = prefsDataStore.data
        .map { it.decode() }
        .distinctUntilChanged()
        .shareIn(scope, SharingStarted.WhileSubscribed(), 1)

      override suspend fun updateData(transform: T.() -> T): T =
        prefsDataStore.updateData {
          val current = decode()
          val update = current.transform()
          if (current != update) {
            toMutableMap().apply {
              PrefsEncoder(EmptySerializersModule, json, serializer.descriptor, this)
                .encodeSerializableValue(serializer, update)
            }
          } else this
        }.decode()
    }
  }

  @Provide
  fun initialOrDefault(initial: () -> @Initial T = default): @InitialOrDefault T = initial()
}

@Provide val prefsDataStoreModule =
  DataStoreModule<Map<String, String?>>("prefs") { emptyMap() }
