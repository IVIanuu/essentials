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

package com.ivianuu.essentials.ui.navigation

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/* Pops the top key on back presses */
@Provide fun androidBackPressHandler(
  activity: ComponentActivity,
  navigator: Navigator
): ScopeWorker<UiScope> = {
  navigator.state
    .map { it.backStack.size > 1 }
    .distinctUntilChanged()
    .flatMapLatest { if (it) activity.backPresses() else infiniteEmptyFlow() }
    .collect { navigator.popTop() }
}

private fun OnBackPressedDispatcherOwner.backPresses() = callbackFlow<Unit> {
  val callback = object : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
      trySend(Unit)
    }
  }
  onBackPressedDispatcher.addCallback(callback)
  awaitClose { callback.remove() }
}

/* Pops to root on activity destroy */
@Provide fun popToRootOnActivityDestroyWorker(navigator: Navigator): ScopeWorker<UiScope> = {
  runOnCancellation {
    navigator.state
      .first()
      .backStack
      .drop(1)
      .forEach { navigator.pop(it) }
  }
}
