/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import androidx.activity.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.lifecycle.compose.currentStateAsState
import essentials.*
import essentials.app.*
import essentials.ui.*
import injekt.*

@Provide @Composable fun AppVisibleScopeManager(
  appVisibleScopeFactory: () -> Scope<AppVisibleScope>,
  activity: ComponentActivity
): ScopeContent<UiScope> {
  if (activity is EsActivity) {
    val state by activity.lifecycle.currentStateAsState()
    if (state.isAtLeast(Lifecycle.State.STARTED))
      DisposableEffect(true) {
        val scope = appVisibleScopeFactory()
        onDispose { scope.dispose() }
      }
  }
}
