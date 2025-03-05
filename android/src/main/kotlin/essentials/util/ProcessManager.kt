/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import androidx.compose.runtime.*
import com.jakewharton.processphoenix.*
import essentials.*
import essentials.logging.*
import injekt.*

@Stable @Provide class ProcessManager(
  private val appContext: AppContext,
  private val logger: Logger
) {
  suspend fun restart() {
    logger.d { "restart process" }
    ProcessPhoenix.triggerRebirth(appContext)
  }
}
