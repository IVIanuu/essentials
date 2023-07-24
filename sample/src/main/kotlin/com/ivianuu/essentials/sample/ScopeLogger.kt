/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.ScopeManager
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide

@Provide fun observer(logger: Logger, scopeManager: ScopeManager) = ScopeWorker<AppScope> {
  scopeManager.activeScopes.collect { activeScopes ->
    logger.log { "active scopes ${activeScopes.map { it.name.value }}" }
  }
}
