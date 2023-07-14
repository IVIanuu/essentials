/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.app.AppForegroundScope
import com.ivianuu.essentials.app.AppForegroundState
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Tag annotation class ForegroundActivityTag {
  @Provide companion object {
    @Provide val foregroundActivityState = MutableStateFlow<ForegroundActivity>(null)
  }
}

typealias ForegroundActivity = @ForegroundActivityTag ComponentActivity?

interface ForegroundActivityMarker

@Provide fun foregroundActivityWorker(
  activity: ComponentActivity,
  coroutineContexts: CoroutineContexts,
  foregroundScopeFactory: () -> Scope<AppForegroundScope>,
  state: MutableStateFlow<ForegroundActivity>
) = ScopeWorker<UiScope> worker@{
  if (activity !is ForegroundActivityMarker) return@worker

  var foregroundScope: Scope<AppForegroundScope>? = null
  val observer = LifecycleEventObserver { _, _ ->
    if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
      state.value = activity
      if (foregroundScope == null)
        foregroundScope = foregroundScopeFactory()
    } else {
      foregroundScope?.dispose()
      foregroundScope = null
      state.value = null
    }
  }

  withContext(coroutineContexts.main) {
    activity.lifecycle.addObserver(observer)
    onCancel { activity.lifecycle.removeObserver(observer) }
  }
}

@Provide fun androidAppForegroundState(
  foregroundActivity: Flow<ForegroundActivity>
): Flow<AppForegroundState> =
  foregroundActivity.map {
    if (it != null) AppForegroundState.FOREGROUND else AppForegroundState.BACKGROUND
  }
