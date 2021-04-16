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

package com.ivianuu.essentials.ui.navigation

import androidx.activity.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

@Given
fun androidBackPressHandler(
    @Given activity: ComponentActivity,
    @Given navigator: Navigator
): ScopeWorker<UiGivenScope> = {
    navigator.state
        .map { it.backStack.size > 1 }
        .distinctUntilChanged()
        .flatMapLatest { if (it) activity.backPresses() else infiniteEmptyFlow() }
        .collect { navigator.popTop() }
}

private fun OnBackPressedDispatcherOwner.backPresses() = callbackFlow<Unit> {
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            offer(Unit)
        }
    }
    onBackPressedDispatcher.addCallback(callback)
    awaitClose { callback.remove() }
}
