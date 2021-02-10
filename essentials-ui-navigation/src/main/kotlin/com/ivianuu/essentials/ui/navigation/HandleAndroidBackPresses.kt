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

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import com.ivianuu.essentials.coroutines.neverFlow
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.UiWorkerBinding

import com.ivianuu.essentials.ui.uiWorkerBinding
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@UiWorkerBinding
@GivenFun
suspend fun handleAndroidBackPresses(
    @Given activity: ComponentActivity,
    @Given dispatchNavigation: DispatchAction<NavigationAction>,
    @Given navigationState: Flow<NavigationState>,
) {
    navigationState
        .map { it.backStack.size > 1 }
        .distinctUntilChanged()
        .flatMapLatest { if (it) activity.backPresses() else neverFlow() }
        .onEach { dispatchNavigation(NavigationAction.PopTop()) }
        .collect()
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
