/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*

@Provide fun scopeLogger(logger: Logger, appScope: Scope<AppScope>) = ScopeComposition<AppScope> {
  val activeScopes = appScope.allScopes
  LaunchedEffect(activeScopes) {
    logger.d { "active scopes ${activeScopes.map { it.name.simpleName }}" }
  }
}
