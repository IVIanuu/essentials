/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.allScopes
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide

@Provide fun observer(logger: Logger, appScope: Scope<AppScope>) = ScopeWorker<AppScope> {
  appScope.allScopes().collect { allScopes ->
    logger.log { "active scopes ${allScopes.map { it.name.value }}" }
  }
}
