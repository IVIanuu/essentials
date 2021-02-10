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

import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.coroutines.lens
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction.*
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Scoped<AppComponent>
@Given
fun navigationState(
    @Given intentKeyHandler: intentKeyHandler,
    @Given scope: GlobalScope,
    @Given initial: @Initial NavigationState = NavigationState(),
    @Given actions: Actions<NavigationAction>
): StateFlow<NavigationState> = scope.state(InternalNavigationState(initial.backStack, emptyMap())) {
    actions
        .filterIsInstance<Push>()
        .onEach { action ->
            if (!intentKeyHandler(action.key)) {
                reduce {
                    copy(
                        backStack = backStack + action.key,
                        results = if (action.deferredResult != null) {
                            results + mapOf(action.key to action.deferredResult)
                        } else results
                    )
                }
            }
        }
        .launchIn(this)

    actions
        .filterIsInstance<ReplaceTop>()
        .onEach { action ->
            if (intentKeyHandler(action.key)) {
                reduce {
                    copy(
                        backStack = backStack.dropLast(1),
                        results = if (action.deferredResult != null) {
                            results + mapOf(action.key to action.deferredResult)
                        } else results
                    )
                }
            } else {
                reduce {
                    copy(
                        backStack = backStack.dropLast(1) + action.key,
                        results = if (action.deferredResult != null) {
                            results + mapOf(action.key to action.deferredResult)
                        } else results
                    )
                }
            }
        }
        .launchIn(this)

    actions
        .filterIsInstance<Pop>()
        .reduce { popKey(it.key, it.result) }
        .launchIn(this)

    actions
        .filterIsInstance<PopTop>()
        .onEach { action ->
            val topKey = currentState().backStack.last()
            reduce { popKey(topKey, action.result) }
        }
        .launchIn(this)
}.lens { NavigationState(it.backStack) }

private fun InternalNavigationState.popKey(
    key: Key,
    result: Any?,
): InternalNavigationState {
    @Suppress("UNCHECKED_CAST")
    val deferredResult = results[key] as? CompletableDeferred<Any?>
    deferredResult?.complete(result)
    return copy(
        backStack = backStack - key,
        results = results - key
    )
}

private data class InternalNavigationState(
    val backStack: List<Key>,
    val results: Map<Key, CompletableDeferred<out Any?>>,
)
