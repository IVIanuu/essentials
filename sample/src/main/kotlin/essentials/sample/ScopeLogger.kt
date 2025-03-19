/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample

import androidx.compose.runtime.*
import essentials.*
import injekt.*

@Provide @Composable fun ActiveScopeLogger(
  logger: Logger = inject,
  appScope: Scope<AppScope>
): ScopeContent<AppScope> {
  val activeScopes = appScope.allScopes
  LaunchedEffect(activeScopes) {
    d { "active scopes ${activeScopes.map { it.name.simpleName }}" }
  }
}
