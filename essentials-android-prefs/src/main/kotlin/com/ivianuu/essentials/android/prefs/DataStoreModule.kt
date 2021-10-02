package com.ivianuu.essentials.android.prefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.InitialOrDefault
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.actAndReply
import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.fold
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.IODispatcher
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

class DataStoreModule<T : Any>(private val name: String, private val default: () -> T) {
  @Provide fun dataStore(
    dispatcher: IODispatcher,
    jsonFactory: () -> Json,
    initial: () -> @Initial T = default,
    serializerFactory: () -> KSerializer<T>,
    prefsDir: () -> PrefsDir,
    scope: NamedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<T> {
    val dataStore = DataStoreFactory.create(
      produceFile = { prefsDir().resolve(name) },
      serializer = object : Serializer<T> {
        override val defaultValue: T
          get() = initial()
        private val json by lazy(jsonFactory)
        private val serializer by lazy(serializerFactory)
        override suspend fun readFrom(input: InputStream): T = catch {
          json.decodeFromStream(serializer, input)
        }.fold(
          success = { it },
          failure = { throw CorruptionException("Couldn't deserialize data", it) }
        )

        override suspend fun writeTo(t: T, output: OutputStream) {
          json.encodeToStream(serializer, t, output)
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

      override suspend fun updateData(transform: T.() -> T): T = actor.actAndReply {
        dataStore.updateData { transform(it) }
      }
    }
  }

  @Provide
  fun initialOrDefault(initial: () -> @Initial T = default): @InitialOrDefault T = initial()
}
