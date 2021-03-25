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

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ivianuu.essentials.activity.EsActivity
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.ActivityGivenScope
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

typealias ForegroundActivity = ComponentActivity?

@Given
val foregroundActivityState: @Scoped<AppGivenScope> MutableStateFlow<ForegroundActivity>
    get() = MutableStateFlow(null)

@Given
fun foregroundActivityStateWorker(
    @Given activity: ComponentActivity,
    @Given dispatcher: MainDispatcher,
    @Given state: MutableStateFlow<ForegroundActivity>
): ScopeWorker<ActivityGivenScope> = worker@ {
    if (activity !is EsActivity) return@worker
    val observer = LifecycleEventObserver { _, _ ->
        state.value = if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
            activity else null
    }
    withContext(dispatcher) {
        activity.lifecycle.addObserver(observer)
        runOnCancellation { activity.lifecycle.removeObserver(observer) }
    }
}
