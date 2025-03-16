/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import com.jakewharton.processphoenix.*
import essentials.*
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.*

@Tag annotation class restartProcessResult
typealias restartProcess = suspend () -> @restartProcessResult Nothing

@Provide suspend fun restartProcess(
  appContext: AppContext,
  logger: Logger = inject
): @restartProcessResult Nothing {
  d { "restart process" }
  ProcessPhoenix.triggerRebirth(appContext)
  awaitCancellation()
}
