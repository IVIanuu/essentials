package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.InitialOrDefault
import com.ivianuu.essentials.coroutines.childCoroutineContext
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.data.DiskDataStore
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.data.ReplaceDataCorruptionHandler
import com.ivianuu.essentials.data.Serializer
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.ComponentScope
import com.ivianuu.injekt.coroutines.IODispatcher

class DataStoreModule<T : Any>(private val name: String, private val default: () -> T) {
  @Provide fun dataStore(
    dispatcher: IODispatcher,
    initial: () -> @Initial T = default,
    serializerFactory: () -> Serializer<T>,
    prefsDir: () -> PrefsDir,
    scope: ComponentScope<AppComponent>
  ): @Scoped<AppComponent> DataStore<T> = DiskDataStore(
    coroutineContext = scope.coroutineContext.childCoroutineContext(dispatcher),
    produceFile = { prefsDir().resolve(name) },
    produceSerializer = serializerFactory,
    corruptionHandler = ReplaceDataCorruptionHandler { _, error ->
      error.printStackTrace()
      initial()
    }
  )

  @Provide
  fun initialOrDefault(initial: () -> @Initial T = default): @InitialOrDefault T = initial()
}
