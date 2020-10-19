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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ApplicationLifecycleOwner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

typealias AppForegroundState = Flow<Boolean>
@Binding
fun appForegroundState(
    mainDispatcher: MainDispatcher,
    lifecycleOwner: ApplicationLifecycleOwner,
): AppForegroundState = callbackFlow {
    var wasInForeground = false
    val observer = LifecycleEventObserver { source, _ ->
        val isInForeground = source.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        if (wasInForeground != isInForeground) {
            wasInForeground = isInForeground
            offer(isInForeground)
        }
    }
    offer(wasInForeground)
    lifecycleOwner.lifecycle.addObserver(observer)
    awaitClose { lifecycleOwner.lifecycle.removeObserver(observer) }
}.flowOn(mainDispatcher)
