/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.jakewharton.processphoenix.ProcessPhoenix

@Provide class ProcessRestarter(
  private val appContext: AppContext,
  private val logger: Logger,
) {
  suspend operator fun invoke() {
    logger.log { "restart process" }
    ProcessPhoenix.triggerRebirth(appContext)
  }
}
