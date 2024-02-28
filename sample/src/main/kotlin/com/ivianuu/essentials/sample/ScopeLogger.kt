/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import androidx.compose.runtime.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.injekt.*

@Provide fun scopeLogger(logger: Logger, scopeManager: ScopeManager) = ScopeComposition<AppScope> {
  val activeScopes = scopeManager.activeScopes
  LaunchedEffect(activeScopes) {
    logger.d { "active scopes ${activeScopes.map { it.name.simpleName }}" }
  }
}
