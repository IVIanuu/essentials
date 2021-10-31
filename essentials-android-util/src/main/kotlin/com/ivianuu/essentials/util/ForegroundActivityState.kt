/*
 * Copyright 2021 Manuel Wrage
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

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.MainDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

typealias ForegroundActivity = ComponentActivity?

interface ForegroundActivityMarker

@Provide @Scoped<AppComponent> val foregroundActivityState: MutableStateFlow<ForegroundActivity>
  get() = MutableStateFlow(null)

@Provide fun foregroundActivityStateWorker(
  activity: ComponentActivity,
  mainDispatcher: MainDispatcher,
  state: MutableStateFlow<ForegroundActivity>
): ScopeWorker<ActivityComponent> = worker@ {
  if (activity !is ForegroundActivityMarker) return@worker
  val observer = LifecycleEventObserver { _, _ ->
    state.value = if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
      activity else null
  }
  withContext(mainDispatcher) {
    activity.lifecycle.addObserver(observer)
    onCancel { activity.lifecycle.removeObserver(observer) }
  }
}
