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

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface StateScope<S> : CoroutineScope {
    val state: Flow<S>

    suspend fun reduce(reducer: S.() -> S): S

    fun Flow<S.() -> S>.reduce(): Flow<S.() -> S> =
        onEach { this@StateScope.reduce(it) }

    fun <T> Flow<T>.reduce(reducer: S.(T) -> S): Flow<T> =
        onEach { value ->
            this@StateScope.reduce { reducer(value) }
        }
}

suspend inline fun <S> StateScope<S>.currentState(): S = state.first()

suspend inline fun <S> FlowCollector<S.() -> S>.reduce(noinline reducer: S.() -> S) = emit(reducer)

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
    this@state.reduce().collect()
}

fun <S> CoroutineScope.state(
    initial: S,
    started: SharingStarted = SharingStarted.Lazily,
    block: suspend StateScope<S>.() -> Unit
): StateFlow<S> = state(MutableStateFlow(initial), started, block)

fun <S> CoroutineScope.state(
    initial: S,
    state: Flow<S>,
    setState: suspend (S) -> Unit,
    started: SharingStarted = SharingStarted.Lazily,
    block: suspend StateScope<S>.() -> Unit
): StateFlow<S> {
    val _state = state.stateIn(this, started, initial)
    return state(
        _state,
        { _state.value },
        setState,
        started,
        block
    )
}

fun <S> CoroutineScope.state(
    state: MutableStateFlow<S>,
    started: SharingStarted = SharingStarted.Lazily,
    block: suspend StateScope<S>.() -> Unit
): StateFlow<S> = state(
    state,
    { state.value },
    { state.value = it },
    started,
    block
)

fun <S> CoroutineScope.state(
    state: Flow<S>,
    getState: () -> S,
    setState: suspend (S) -> Unit,
    started: SharingStarted = SharingStarted.Lazily,
    block: suspend StateScope<S>.() -> Unit
): StateFlow<S> = channelFlow {
    coroutineScope {
        launch {
            state.collect { newState ->
                send(newState)
            }
        }
        StateScopeImpl(this, state, getState, setState).block()
    }
}.stateIn(this, started, getState())

private class StateScopeImpl<S>(
    scope: CoroutineScope,
    override val state: Flow<S>,
    private val getState: () -> S,
    private val setState: suspend (S) -> Unit
) : StateScope<S>, CoroutineScope by scope {

    private val actor = actor<Reduce>(
        start = CoroutineStart.LAZY,
        capacity = Channel.UNLIMITED
    ) {
        for (reduce in this) {
            val currentState = getState()
            val newState = reduce.reducer(currentState)
            if (currentState != newState) setState(newState)
            reduce.acknowledged.complete(newState)
        }
    }

    override suspend fun reduce(reducer: S.() -> S): S {
        val acknowledged = CompletableDeferred<S>()
        actor.offer(Reduce(reducer, acknowledged))
        return acknowledged.await()
    }

    private inner class Reduce(
        val reducer: S.() -> S,
        val acknowledged: CompletableDeferred<S>
    )
}
