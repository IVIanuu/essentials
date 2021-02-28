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
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.navigation.NavigationAction.PopTop
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.NavigationAction.ReplaceTop
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Scoped<AppComponent>
@Given
fun navigationState(
    @Given IntentKeyHandler: IntentKeyHandler,
    @Given scope: GlobalScope,
    @Given initial: @Initial NavigationState = NavigationState(),
    @Given actions: Actions<NavigationAction>
): StateFlow<NavigationState> = scope.state(InternalNavigationState(initial.backStack, emptyMap())) {
    actions
        .filterIsInstance<Push<Any>>()
        .onEach { action ->
            if (!IntentKeyHandler(action.key)) {
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
        .filterIsInstance<ReplaceTop<Any>>()
        .onEach { action ->
            if (IntentKeyHandler(action.key)) {
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
        .filterIsInstance<Pop<Any>>()
        .reduce { popKey(it.key, it.result) }
        .launchIn(this)

    actions
        .filterIsInstance<PopTop>()
        .onEach {
            val topKey = currentState().backStack.last()
            reduce {
                @Suppress("UNCHECKED_CAST")
                popKey(topKey as Key<Any>, null)
            }
        }
        .launchIn(this)
}.lens { NavigationState(it.backStack) }

private fun <R : Any> InternalNavigationState.popKey(
    key: Key<R>,
    result: R?,
): InternalNavigationState {
    @Suppress("UNCHECKED_CAST")
    val deferredResult = results[key] as? CompletableDeferred<R?>
    deferredResult?.complete(result)
    return copy(
        backStack = backStack - key,
        results = results - key
    )
}

private data class InternalNavigationState(
    val backStack: List<Key<*>>,
    val results: Map<Key<*>, CompletableDeferred<out Any?>>,
)
