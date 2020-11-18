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

import com.ivianuu.essentials.coroutines.EventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

interface StateScope<S> {
    val state: StateFlow<S>

    fun Flow<Reducer<S>>.reduce()

    fun <T> Flow<T>.reduce(reducer: S.(T) -> S) =
        mapReduce(reducer).reduce()

    fun reducerFlow(block: suspend FlowCollector<Reducer<S>>.() -> Unit) =
        flow(block).reduce()

    fun effect(block: suspend () -> Unit)

    fun <T> Flow<T>.effect(action: suspend (T) -> Unit) = this@StateScope.effect {
        collect(action)
    }

    fun reduce(reducer: Reducer<S>)

    fun <T> Flow<T>.mapReduce(reducer: S.(T) -> S): Flow<Reducer<S>> =
        map { value -> { reducer(value) } }

    fun Flow<Reducer<S>>.catchReduce(reducer: S.(Throwable) -> S): Flow<Reducer<S>> =
        catch { t -> emit { reducer(t) } }


    fun <T> Flow<T>.reduceCatching(
        success: S.(T) -> S,
        error: S.(Throwable) -> S
    ) = mapReduce(success).catchReduce(error).reduce()
}

suspend inline fun <S> FlowCollector<Reducer<S>>.reduce(noinline reducer: S.() -> S) = emit(reducer)

fun <S> CoroutineScope.state(
    initial: S,
    start: Flow<*> = flowOf(Unit),
    block: StateScope<S>.() -> Unit
): StateFlow<S> = state(MutableStateFlow(initial), start, block)

fun <S> CoroutineScope.state(
    state: MutableStateFlow<S>,
    start: Flow<*> = flowOf(Unit),
    block: StateScope<S>.() -> Unit
): StateFlow<S> = state(state, { state.value = it }, start, block)

fun <S> CoroutineScope.state(
    state: Flow<S>,
    initial: S,
    setState: suspend (S) -> Unit,
    start: Flow<*> = flowOf(Unit),
    block: StateScope<S>.() -> Unit
): StateFlow<S> = state(
    state.stateIn(this, SharingStarted.Eagerly, initial),
    setState,
    start,
    block
)

fun <S> CoroutineScope.state(
    state: StateFlow<S>,
    setState: suspend (S) -> Unit,
    start: Flow<*> = flowOf(Unit),
    block: StateScope<S>.() -> Unit
): StateFlow<S> {
    val stateScope = StateScopeImpl(state)
        .apply(block)

    val reducers = stateScope.reducers.merge()
    val effects = stateScope.effects

    launch {
        start
            .take(1)
            .first()
        launch {
            reducers.collect { reducer ->
                val currentState = state.value
                val newState = reducer(currentState)
                if (currentState != newState) setState(newState)
            }
        }
        effects.forEach { effect ->
            launch {
                effect()
            }
        }
    }

    return state
}

private class StateScopeImpl<S>(override val state: StateFlow<S>) : StateScope<S> {
    private val reduces = EventFlow<Reducer<S>>()
    val reducers = mutableListOf<Flow<Reducer<S>>>(reduces)
    val effects = mutableListOf<suspend () -> Unit>()
    override fun Flow<Reducer<S>>.reduce() {
        reducers += this
    }

    override fun effect(block: suspend () -> Unit) {
        effects += block
    }

    override fun reduce(reducer: Reducer<S>) {
        reduces.emit(reducer)
    }
}

suspend inline fun <S> StateScope<S>.currentState(): S = state.first()

typealias Reducer<S> = S.() -> S
