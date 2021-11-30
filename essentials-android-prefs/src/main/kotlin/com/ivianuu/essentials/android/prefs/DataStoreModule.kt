/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*

class DataStoreModule<T : Any>(private val name: String, private val default: () -> T) {
  @Provide fun dataStore(
    context: IOContext,
    initial: () -> @Initial T = default,
    serializerFactory: () -> Serializer<T>,
    prefsDir: () -> PrefsDir,
    scope: NamedCoroutineScope<AppScope>
  ): @Scoped<AppScope> DataStore<T> = DiskDataStore(
    coroutineContext = scope.coroutineContext.childCoroutineContext(context),
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
