/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample

import androidx.compose.runtime.*
import essentials.*
import essentials.logging.*
import injekt.*

@Provide @Composable fun ActiveScopeLogger(scope: Scope<*> = inject): ScopeContent<AppScope> {
  val activeScopes = scope.allScopes
  LaunchedEffect(activeScopes) {
    d { "active scopes ${activeScopes.map { it.name.simpleName }}" }
  }
}
