/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.repeatOnLifecycle
import arrow.fx.coroutines.bracketCase
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.app.AppVisibleScope
import com.ivianuu.essentials.app.EsActivity
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.bracketCase
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext

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
