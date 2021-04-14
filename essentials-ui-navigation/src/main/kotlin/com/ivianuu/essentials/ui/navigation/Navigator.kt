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

import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.mapState
import com.ivianuu.essentials.coroutines.stateStore
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.cast
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface Navigator {
    val state: StateFlow<NavigationState>
    fun push(key: Key<*>)
    suspend fun <R> pushForResult(key: Key<R>): R?
    fun replaceTop(key: Key<*>)
    suspend fun <R> replaceTopForResult(key: Key<R>): R?
    fun <R> pop(key: Key<R>, result: R? = null)
    fun popTop()
}

data class NavigationState(val backStack: List<Key<*>> = emptyList())

@Given
@Scoped<AppGivenScope>
class NavigatorImpl(
    @Given private val intentKeyHandler: IntentKeyHandler,
    @Given private val logger: Logger,
    @Given rootKey: RootKey? = null,
    @Given private val scope: ScopeCoroutineScope<AppGivenScope>
) : Navigator {
    private val store = scope.stateStore(State(listOfNotNull(rootKey)))
    override val state: StateFlow<NavigationState>
        get() = store.mapState { NavigationState(it.backStack) }

    override fun push(key: Key<*>) {
        scope.launch { pushForResult(key) }
    }

    override suspend fun <R> pushForResult(key: Key<R>): R? {
        logger.d { "push $key" }
        val result = CompletableDeferred<R?>()
        if (!intentKeyHandler(key) { result.complete(it as R) }) {
            store.update {
                copy(
                    backStack = backStack + key,
                    results = results + mapOf(key to result)
                )
            }
        }
        return result.await()
    }

    override fun replaceTop(key: Key<*>) {
        scope.launch { replaceTopForResult(key) }
    }

    override suspend fun <R> replaceTopForResult(key: Key<R>): R? {
        val result = CompletableDeferred<R?>()
        logger.d { "replace top $key" }
        if (intentKeyHandler(key) { result.complete(it as R?) }) {
            store.update {
                copy(
                    backStack = backStack.dropLast(1),
                    results = results + mapOf(key to result)
                )
            }
        } else {
            store.update {
                copy(
                    backStack = backStack.dropLast(1) + key,
                    results = results + mapOf(key to result)
                )
            }
        }
        return result.await()
    }

    override fun <R> pop(key: Key<R>, result: R?) {
        scope.launch {
            logger.d { "pop $key" }
            store.update { popKey(key, result) }
        }
    }

    override fun popTop() {
        scope.launch {
            val topKey = state.first().backStack.last()
            logger.d { "pop top $topKey" }
            store.update {
                @Suppress("UNCHECKED_CAST")
                popKey(topKey as Key<Any>, null)
            }
        }
    }

    data class State(
        val backStack: List<Key<*>> = emptyList(),
        val results: Map<Key<*>, CompletableDeferred<out Any?>> = emptyMap(),
    )

    private fun <R> State.popKey(key: Key<R>, result: R?): State {
        @Suppress("UNCHECKED_CAST")
        val resultAction = results[key] as? CompletableDeferred<R?>
        resultAction?.complete(result)
        return copy(
            backStack = backStack - key,
            results = results - key
        )
    }
}
