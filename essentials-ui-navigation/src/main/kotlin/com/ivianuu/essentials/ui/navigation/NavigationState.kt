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

import android.content.Intent
import com.ivianuu.essentials.coroutines.lens
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.navigation.NavigationAction.*
import com.ivianuu.essentials.ui.store.GlobalStateBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@GlobalStateBinding
fun navigationState(
    applicationContext: ApplicationContext,
    intentFactories: KeyIntentFactories,
    scope: CoroutineScope,
    initial: @Initial NavigationState = NavigationState(),
    actions: Actions<NavigationAction>,
): StateFlow<@Initial NavigationState> {
    return scope.state(InternalNavigationState(initial.backStack, emptyMap())) {
        actions
            .onEach { action ->
                when (action) {
                    is Push -> {
                        val intentFactory = intentFactories[action.key::class]?.invoke()
                        if (intentFactory != null) {
                            val intent = intentFactory(action.key)
                            applicationContext.startActivity(
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        } else {
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
                    is ReplaceTop -> {
                        val intentFactory = intentFactories[action.key::class]?.invoke()
                        if (intentFactory != null) {
                            val intent = intentFactory(action.key)
                            applicationContext.startActivity(
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
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
                    is Pop -> {
                        val deferredResult = currentState().results[action.key]
                        deferredResult?.complete(action.result)
                        reduce {
                            copy(
                                backStack = backStack - action.key,
                                results = results - action.key
                            )
                        }
                    }
                    is PopTop -> {
                        val topKey = currentState().backStack.last()
                        val deferredResult = currentState().results[topKey]
                        deferredResult?.complete(action.result)
                        reduce {
                            copy(
                                backStack = backStack - topKey,
                                results = results - topKey
                            )
                        }
                    }
                }
            }
            .launchIn(this)
    }.lens { NavigationState(it.backStack) }
}

private data class InternalNavigationState(
    val backStack: List<Key>,
    val results: Map<Key, CompletableDeferred<Any?>>,
)
