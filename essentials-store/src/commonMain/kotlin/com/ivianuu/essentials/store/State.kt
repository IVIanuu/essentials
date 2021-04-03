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

package com.ivianuu.essentials.store

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

interface StateScope<S> : CoroutineScope {
    val state: Flow<S>

    suspend fun update(reducer: S.() -> S): S

    fun Flow<S.() -> S>.update(): Flow<S.() -> S> =
        onEach { this@StateScope.update(it) }

    fun <T> Flow<T>.update(reducer: S.(T) -> S): Flow<T> =
        onEach { value ->
            this@StateScope.update { reducer(value) }
        }
}

fun <T, S> Flow<T>.state(
    scope: CoroutineScope,
    initial: S,
    started: SharingStarted = SharingStarted.Lazily,
    reducer: S.(T) -> S
) = map<T, S.() -> S> { value -> { reducer(value) } }
    .state(scope, initial, started)

fun <S> Flow<S.() -> S>.state(
    scope: CoroutineScope,
    initial: S,
    started: SharingStarted = SharingStarted.Lazily
): StateFlow<S> = scope.state(initial, started) {
    this@state.update().collect()
}

fun <S> CoroutineScope.state(
    initial: S,
    started: SharingStarted = SharingStarted.Lazily,
    block: suspend StateScope<S>.() -> Unit
): StateFlow<S> {
    val state = MutableStateFlow(initial)
    val stateScope = object : StateScope<S>, CoroutineScope by this {
        override val state: Flow<S>
            get() = state
        override suspend fun update(reducer: S.() -> S): S = synchronized(state) {
            val currentState = state.value
            val newState = reducer(currentState)
            if (currentState != newState) state.value = newState
            newState
        }
    }
    stateScope.launch {
        started.command(state.subscriptionCount)
            .distinctUntilChanged()
            .collectLatest { command ->
                when (command) {
                    SharingCommand.START -> stateScope.block()
                    SharingCommand.STOP_AND_RESET_REPLAY_CACHE -> state.resetReplayCache()
                    SharingCommand.STOP -> {
                        // nothing to do because collectLatest cancels the previous block
                    }
                }

            }
    }
    return state
}
