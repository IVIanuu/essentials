/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.InitialOrDefault
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.ComponentScope
import com.ivianuu.injekt.coroutines.IODispatcher
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
  @Provide @Scoped<AppComponent> fun dataStore(
    prefsDataStore: DataStore<Map<String, String?>>,
    dispatcher: IODispatcher,
    jsonFactory: () -> Json,
    initial: () -> @Initial T = default,
    serializerFactory: () -> KSerializer<T>,
    scope: ComponentScope<AppComponent>
  ): DataStore<T> {
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
