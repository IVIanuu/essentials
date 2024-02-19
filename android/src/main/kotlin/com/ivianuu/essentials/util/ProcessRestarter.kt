/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.jakewharton.processphoenix.*

@Provide class ProcessRestarter(private val appContext: AppContext, private val logger: Logger) {
  suspend operator fun invoke() {
    logger.log { "restart process" }
    ProcessPhoenix.triggerRebirth(appContext)
  }
}
