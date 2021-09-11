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

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.github.michaelbull.result.fold
import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.InitialOrDefault
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.actAndReply
import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class PrefModule<T : Any>(private val name: String, private val default: () -> T) {
  @Provide fun dataStore(
    dispatcher: IODispatcher,
    jsonFactory: () -> Json,
    initial: () -> @Initial T = default,
    serializerFactory: () -> KSerializer<T>,
    prefsDir: () -> PrefsDir,
    scope: InjektCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<T> {
    val dataStore = DataStoreFactory.create(
      produceFile = { prefsDir().resolve(name) },
      serializer = object : Serializer<T> {
        override val defaultValue: T
          get() = initial()
        private val json by lazy(jsonFactory)
        private val serializer by lazy(serializerFactory)
        override suspend fun readFrom(input: InputStream): T = catch {
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

  @Provide
  fun initialOrDefault(initial: () -> @Initial T = default): @InitialOrDefault T = initial()
}
