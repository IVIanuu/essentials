/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util

import androidx.activity.*
import androidx.lifecycle.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

typealias ForegroundActivity = ComponentActivity?

interface ForegroundActivityMarker

@Provide val foregroundActivityState: @Scoped<AppScope> MutableStateFlow<ForegroundActivity>
  get() = MutableStateFlow(null)

@Provide fun foregroundActivityStateWorker(
  activity: ComponentActivity,
  dispatcher: MainDispatcher,
  state: MutableStateFlow<ForegroundActivity>
): ScopeWorker<ActivityScope> = worker@{
  if (activity !is ForegroundActivityMarker) return@worker
  val observer = LifecycleEventObserver { _, _ ->
    state.value = if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
      activity else null
  }
  withContext(dispatcher) {
    activity.lifecycle.addObserver(observer)
    runOnCancellation { activity.lifecycle.removeObserver(observer) }
  }
}
