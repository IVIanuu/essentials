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
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.ResultAction
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.mapState
import com.ivianuu.essentials.store.onAction
import com.ivianuu.essentials.store.sendAndAwait
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.navigation.NavigationAction.PopTop
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.ui.navigation.NavigationAction.ReplaceTop
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

data class NavigationState(val backStack: List<Key<*>> = emptyList())

sealed class NavigationAction {
    data class Push<R>(val key: Key<R>) : NavigationAction(), ResultAction<R?> by ResultAction()
    data class ReplaceTop<R>(val key: Key<R>) : NavigationAction(), ResultAction<R?> by ResultAction()
    data class Pop<R>(val key: Key<R>, val result: R? = null) : NavigationAction()
    object PopTop : NavigationAction()
}

fun Sink<NavigationAction>.push(key: Key<*>) = send(Push(key))

suspend fun <R : Any> Sink<Push<R>>.pushAndAwait(key: Key<R>): R? = sendAndAwait(Push(key))

fun Sink<NavigationAction>.replaceTop(key: Key<*>) = send(ReplaceTop(key))

fun <R> Sink<NavigationAction>.pop(key: Key<R>, result: R? = null) = send(Pop(key, result))

fun Sink<NavigationAction>.popTop() = send(PopTop)

@Given
fun navigationStore(
    @Given intentKeyHandler: IntentKeyHandler,
    @Given logger: Logger,
    @Given initial: @Initial InternalNavigationState,
    @Given actions: MutableSharedFlow<NavigationAction>,
    @Given scope: ScopeCoroutineScope<AppGivenScope>
): @Scoped<AppGivenScope> Store<NavigationState, NavigationAction> = scope.store(initial, actions) {
    onAction<Push<*>> { action ->
        logger.d { "push ${action.key}" }
        if (!intentKeyHandler(action.key)) {
            update {
                copy(
                    backStack = backStack + action.key,
                    resultActions = resultActions + mapOf(action.key to action)
                )
            }
        }
    }
    onAction<ReplaceTop<*>> { action ->
        logger.d { "replace top ${action.key}" }
        if (intentKeyHandler(action.key)) {
            update {
                copy(
                    backStack = backStack.dropLast(1),
                    resultActions = resultActions + mapOf(action.key to action)
                )
            }
        } else {
            update {
                copy(
                    backStack = backStack.dropLast(1) + action.key,
                    resultActions = resultActions + mapOf(action.key to action)
                )
            }
        }
    }
    onAction<Pop<Any>> { action ->
        logger.d { "pop $action.key" }
        update { popKey(action.key, action.result) }
    }
    onAction<PopTop> {
        val topKey = state.first().backStack.last()
        logger.d { "pop top $topKey" }
        update {
            @Suppress("UNCHECKED_CAST")
            popKey(topKey as Key<Any>, null)
        }
    }
}.mapState { NavigationState(it.backStack) }

@Given
val navigationActions: @Scoped<AppGivenScope> MutableSharedFlow<NavigationAction>
    get() = EventFlow()

private fun <R : Any> InternalNavigationState.popKey(key: Key<R>, result: R?): InternalNavigationState {
    @Suppress("UNCHECKED_CAST")
    val resultAction = resultActions[key] as? ResultAction<R?>
    resultAction?.set(result)
    return copy(
        backStack = backStack - key,
        resultActions = resultActions - key
    )
}

data class InternalNavigationState(
    val backStack: List<Key<*>> = emptyList(),
    val resultActions: Map<Key<*>, ResultAction<out Any?>> = emptyMap(),
) {
    companion object {
        @Given
        fun initial(@Given rootKey: RootKey? = null): @Initial InternalNavigationState =
            InternalNavigationState(listOfNotNull(rootKey))
    }
}
