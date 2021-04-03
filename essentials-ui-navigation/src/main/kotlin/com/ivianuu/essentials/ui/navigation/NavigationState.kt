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

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.lens
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.navigation.NavigationAction.PopTop
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.NavigationAction.ReplaceTop
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class NavigationState(val backStack: List<Key<*>> = emptyList())

@Given
fun initialNavigationState(@Given homeKey: HomeKey? = null): @Initial NavigationState =
    NavigationState(listOfNotNull(homeKey))

sealed class NavigationAction {
    data class Push<R : Any>(
        val key: Key<R>,
        val deferredResult: CompletableDeferred<R?>? = null,
    ) : NavigationAction()
    data class Pop<R : Any>(
        val key: Key<R>,
        val result: R? = null,
    ) : NavigationAction()
    object PopTop : NavigationAction()
    data class ReplaceTop<R : Any>(
        val key: Key<R>,
        val deferredResult: CompletableDeferred<R?>? = null,
    ) : NavigationAction()
}

@Given
fun navigationState(
    @Given scope: ScopeCoroutineScope<AppGivenScope>,
    @Given initial: @Initial NavigationState = NavigationState(),
    @Given actions: Flow<NavigationAction>,
    @Given intentKeyHandler: IntentKeyHandler,
    @Given logger: Logger
): @Scoped<AppGivenScope> StateFlow<NavigationState> = scope.state(
    InternalNavigationState(initial.backStack, emptyMap())) {
    actions
        .filterIsInstance<Push<Any>>()
        .onEach { action ->
            logger.d { "push $action" }
            if (!intentKeyHandler(action.key)) {
                update {
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
            logger.d { "replace top $action" }
            if (intentKeyHandler(action.key)) {
                update {
                    copy(
                        backStack = backStack.dropLast(1),
                        results = if (action.deferredResult != null) {
                            results + mapOf(action.key to action.deferredResult)
                        } else results
                    )
                }
            } else {
                update {
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
        .update {
            logger.d { "pop $it" }
            popKey(it.key, it.result)
        }
        .launchIn(this)

    actions
        .filterIsInstance<PopTop>()
        .onEach {
            val topKey = state.first().backStack.last()
            logger.d { "pop top $topKey" }
            update {
                @Suppress("UNCHECKED_CAST")
                popKey(topKey as Key<Any>, null)
            }
        }
        .launchIn(this)
}.lens { NavigationState(it.backStack) }

@Given
val navigationActions = EventFlow<NavigationAction>()

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
