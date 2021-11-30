/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.InitialOrDefault
import com.ivianuu.essentials.coroutines.childCoroutineContext
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.data.DiskDataStore
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.essentials.data.ReplaceDataCorruptionHandler
import com.ivianuu.essentials.data.Serializer
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.IODispatcher
import com.ivianuu.injekt.coroutines.NamedCoroutineScope

class DataStoreModule<T : Any>(private val name: String, private val default: () -> T) {
  @Provide fun dataStore(
    dispatcher: IODispatcher,
    initial: () -> @Initial T = default,
    serializerFactory: () -> Serializer<T>,
    prefsDir: () -> PrefsDir,
    scope: NamedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<T> = DiskDataStore(
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
