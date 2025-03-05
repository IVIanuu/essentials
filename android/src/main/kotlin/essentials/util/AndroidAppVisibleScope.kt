/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import androidx.activity.*
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.*
import essentials.*
import essentials.app.*
import essentials.coroutines.*
import essentials.ui.*
import injekt.*

@Provide fun appVisibleScopeWorker(
  appVisibleScopeFactory: () -> Scope<AppVisibleScope>,
  activity: ComponentActivity
) = ScopeComposition<UiScope> worker@ {
  if (activity is EsActivity)
    LaunchedEffect(true) {
      activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        bracketCase(
          acquire = { appVisibleScopeFactory() },
          release = { scope, _ -> scope.dispose() }
        )
      }
    }
}
