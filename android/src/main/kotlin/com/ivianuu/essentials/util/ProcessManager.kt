/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import com.jakewharton.processphoenix.*

@Provide class ProcessManager(private val appContext: AppContext, private val logger: Logger) {
  suspend fun restart() {
    logger.d { "restart process" }
    ProcessPhoenix.triggerRebirth(appContext)
  }
}
