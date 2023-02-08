/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.coroutines.parForEach
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.invoke
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

@JvmInline value class IsFirstRun(val value: Boolean) {
  companion object {
    private val isFirstRun = CompletableDeferred<IsFirstRun>()

    @Provide fun isFirstRun(pref: DataStore<FirstRunPrefs>) = flow {
      if (!isFirstRun.isCompleted) {
        isFirstRun.complete(IsFirstRun(pref.data.first().isFirstRun))
      }

      emit(isFirstRun.await())
    }
  }
}

fun interface FirstRunHandler {
  suspend operator fun invoke()

  companion object {
    @Provide val defaultHandlers get() = emptyList<FirstRunHandler>()
  }
}

@Provide fun firstRunWorker(
  handlers: () -> List<FirstRunHandler>,
  isFirstRun: Flow<IsFirstRun>,
  logger: Logger,
  pref: DataStore<FirstRunPrefs>
) = ScopeWorker<AppScope> {
  if (!isFirstRun.first().value) return@ScopeWorker

  logger { "first run" }

  handlers().parForEach { it() }

  pref.updateData { copy(isFirstRun = false) }
}

@Serializable data class FirstRunPrefs(val isFirstRun: Boolean = true) {
  companion object {
    @Provide val prefModule = PrefModule { FirstRunPrefs() }
  }
}
