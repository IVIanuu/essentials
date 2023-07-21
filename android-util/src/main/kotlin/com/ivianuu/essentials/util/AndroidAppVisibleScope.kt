/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.app.AppVisibleScope
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.withContext

interface ForegroundActivityMarker

@Provide fun appVisibleScopeWorker(
  appVisibleScopeFactory: () -> Scope<AppVisibleScope>,
  activity: ComponentActivity,
  coroutineContexts: CoroutineContexts,
) = ScopeWorker<UiScope> worker@{
  if (activity !is ForegroundActivityMarker) return@worker

  var appVisibleScope: Scope<AppVisibleScope>? = null
  val observer = LifecycleEventObserver { _, _ ->
    if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
      if (appVisibleScope == null)
        appVisibleScope = appVisibleScopeFactory()
    } else {
      appVisibleScope?.dispose()
      appVisibleScope = null
    }
  }

  withContext(coroutineContexts.main) {
    activity.lifecycle.addObserver(observer)
    onCancel { activity.lifecycle.removeObserver(observer) }
  }
}
