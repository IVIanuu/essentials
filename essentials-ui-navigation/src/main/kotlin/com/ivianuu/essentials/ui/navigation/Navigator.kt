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

import com.ivianuu.essentials.coroutines.lens
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.onAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.*
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

data class NavigationState(val backStack: List<Key<*>> = emptyList())

sealed class NavigationAction {
    data class Push<R>(
        val key: Key<R>,
        val result: CompletableDeferred<R?>? = null
    ) : NavigationAction()
    data class ReplaceTop<R>(
        val key: Key<R>,
        val result: CompletableDeferred<R?>? = null
    ) : NavigationAction()
    data class Pop<R>(val key: Key<R>, val result: R? = null) : NavigationAction()
    object PopTop : NavigationAction()
}

fun Sink<NavigationAction>.push(key: Key<*>) = send(Push(key))

suspend fun <R : Any> Sink<NavigationAction>.pushForResult(key: Key<R>): R? {
    val result = CompletableDeferred<R?>()
    @Suppress("UNCHECKED_CAST")
    send(Push(key, result))
    return result.await()
}

fun Sink<NavigationAction>.replaceTop(key: Key<*>) = send(ReplaceTop(key))

fun <R> Sink<NavigationAction>.pop(key: Key<R>, result: R? = null) = send(Pop(key, result))

fun Sink<NavigationAction>.popTop() = send(PopTop)

@Given
fun navigationStore(
    @Given intentKeyHandler: IntentKeyHandler,
    @Given logger: Logger,
): StoreBuilder<AppGivenScope, InternalNavigationState, NavigationAction> = {
    onAction<Push<*>> { action ->
        logger.d { "push ${action.key}" }
        if (!intentKeyHandler(action.key)) {
            update {
                copy(
                    backStack = backStack + action.key,
                    results = if (action.result != null) {
                        results + mapOf(action.key to action.result)
                    } else results
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
                    results = if (action.result != null) {
                        results + mapOf(action.key to action.result)
                    } else results
                )
            }
        } else {
            update {
                copy(
                    backStack = backStack.dropLast(1) + action.key,
                    results = if (action.result != null) {
                        results + mapOf(action.key to action.result)
                    } else results
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
}

private fun <R : Any> InternalNavigationState.popKey(key: Key<R>, result: R?): InternalNavigationState {
    @Suppress("UNCHECKED_CAST")
    val deferredResult = results[key] as? CompletableDeferred<R?>
    deferredResult?.complete(result)
    return copy(
        backStack = backStack - key,
        results = results - key
    )
}

@Given
fun Store<InternalNavigationState, NavigationAction>.toNavigationState(
) : Store<NavigationState, NavigationAction> = object : Store<NavigationState, NavigationAction>,
    Sink<NavigationAction> by this,
    StateFlow<NavigationState> by (lens { NavigationState(it.backStack) }) {
    }

data class InternalNavigationState(
    val backStack: List<Key<*>> = emptyList(),
    val results: Map<Key<*>, CompletableDeferred<out Any?>> = emptyMap(),
) {
    companion object {
        @Given
        fun initial(@Given rootKey: RootKey? = null): @Initial InternalNavigationState =
            InternalNavigationState(listOfNotNull(rootKey))
    }
}
