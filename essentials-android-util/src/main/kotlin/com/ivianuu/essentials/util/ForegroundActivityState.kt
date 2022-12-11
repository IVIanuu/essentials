/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.coroutines.MainContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

@Tag annotation class ForegroundActivityTag {
  companion object {
    @Provide
    val foregroundActivityState = MutableStateFlow<ForegroundActivity>(null)
  }
}

typealias ForegroundActivity = @ForegroundActivityTag ComponentActivity?

interface ForegroundActivityMarker

@Provide fun foregroundActivityStateWorker(
  activity: ComponentActivity,
  coroutineContext: MainContext,
  state: MutableStateFlow<ForegroundActivity>
) = ScopeWorker<UiScope> worker@ {
  if (activity !is ForegroundActivityMarker) return@worker
  val observer = LifecycleEventObserver { _, _ ->
    state.value = if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
      activity else null
  }
  withContext(coroutineContext) {
    activity.lifecycle.addObserver(observer)
    onCancel { activity.lifecycle.removeObserver(observer) }
  }
}
