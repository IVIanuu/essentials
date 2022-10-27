/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.InitialOrDefault
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule

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
        PrefsDecoder(this, EmptySerializersModule(), serializer.descriptor, json)
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
              PrefsEncoder(EmptySerializersModule(), json, serializer.descriptor, this)
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
