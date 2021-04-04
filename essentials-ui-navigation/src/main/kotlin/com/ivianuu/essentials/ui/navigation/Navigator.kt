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
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

@Scoped<AppGivenScope>
@Given
class Navigator(
    @Given private val intentKeyHandler: IntentKeyHandler,
    @Given private val logger: Logger,
    @Given private val store: ScopeStateStore<AppGivenScope, InternalNavigationState>
) : StateFlow<NavigationState> by store.lens({ NavigationState(it.backStack) }) {
    fun <R : Any> push(key: Key<R>, deferredResult: CompletableDeferred<R?>? = null) = store.effect {
        logger.d { "push $key" }
        if (!intentKeyHandler(key)) {
            update {
                copy(
                    backStack = backStack + key,
                    results = if (deferredResult != null) {
                        results + mapOf(key to deferredResult)
                    } else results
                )
            }
        }
    }

    suspend fun <R : Any> pushForResult(key: Key<R>): R? {
        val result = CompletableDeferred<R?>()
        @Suppress("UNCHECKED_CAST")
        push(key, result)
        return result.await()
    }

    fun <R : Any> replaceTop(key: Key<R>, deferredResult: CompletableDeferred<R?>? = null) = store.effect {
        logger.d { "replace top $key" }
        if (intentKeyHandler(key)) {
            update {
                copy(
                    backStack = backStack.dropLast(1),
                    results = if (deferredResult != null) {
                        results + mapOf(key to deferredResult)
                    } else results
                )
            }
        } else {
            update {
                copy(
                    backStack = backStack.dropLast(1) + key,
                    results = if (deferredResult != null) {
                        results + mapOf(key to deferredResult)
                    } else results
                )
            }
        }
    }

    fun <R : Any> pop(key: Key<R>, result: R? = null) = store.effect {
        logger.d { "pop $key" }
        update { popKey(key, result) }
    }

    fun popTop() = store.effect {
        val topKey = store.first().backStack.last()
        logger.d { "pop top $topKey" }
        update {
            @Suppress("UNCHECKED_CAST")
            popKey(topKey as Key<Any>, null)
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
}

data class NavigationState(val backStack: List<Key<*>> = emptyList()) {
    companion object {
        @Given
        fun initial(@Given rootKey: RootKey? = null): @Initial NavigationState =
            NavigationState(listOfNotNull(rootKey))
    }

}

data class InternalNavigationState(
    val backStack: List<Key<*>>,
    val results: Map<Key<*>, CompletableDeferred<out Any?>>,
) : State()
