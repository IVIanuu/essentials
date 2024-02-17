/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.activity.*
import androidx.lifecycle.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*

@Provide fun appVisibleScopeWorker(
  appVisibleScopeFactory: () -> Scope<AppVisibleScope>,
  activity: ComponentActivity,
  coroutineContexts: CoroutineContexts,
) = ScopeWorker<UiScope> worker@{
  if (activity is EsActivity)
    activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
      bracketCase(
        acquire = { appVisibleScopeFactory() },
        release = { scope, _ -> scope.dispose() }
      )
    }
}
