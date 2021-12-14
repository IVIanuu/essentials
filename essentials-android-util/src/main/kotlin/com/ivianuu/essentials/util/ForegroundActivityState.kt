/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import androidx.activity.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Tag annotation class ForegroundActivityTag

typealias ForegroundActivity = @ForegroundActivityTag ComponentActivity?

interface ForegroundActivityMarker

@Provide
val foregroundActivityState = MutableStateFlow<ForegroundActivity>(null)

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
