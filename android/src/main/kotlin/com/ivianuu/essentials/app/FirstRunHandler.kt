/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import arrow.fx.coroutines.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

@JvmInline value class IsFirstRun(val value: Boolean) {
  @Provide companion object {
    private val isFirstRun = CompletableDeferred<IsFirstRun>()

    @Provide fun isFirstRun(pref: DataStore<FirstRunPrefs>): suspend () -> IsFirstRun = {
      if (!isFirstRun.isCompleted)
        isFirstRun.complete(IsFirstRun(pref.data.first().isFirstRun))

      isFirstRun.await()
    }
  }
}

fun interface FirstRunHandler {
  suspend fun onFirstRun()

  @Provide companion object {
    @Provide val defaultHandlers get() = emptyList<FirstRunHandler>()
  }
}

@Provide fun firstRunWorker(
  handlers: () -> List<FirstRunHandler>,
  isFirstRun: suspend () -> IsFirstRun,
  logger: Logger,
  pref: DataStore<FirstRunPrefs>
) = ScopeWorker<AppScope> {
  if (!isFirstRun().value) return@ScopeWorker

  logger.d { "first run" }

  handlers().parMap { it.onFirstRun() }

  pref.updateData { copy(isFirstRun = false) }
}

@Serializable data class FirstRunPrefs(val isFirstRun: Boolean = true) {
  @Provide companion object {
    @Provide val dataStoreModule = DataStoreModule("first_run_prefs") { FirstRunPrefs() }
  }
}
