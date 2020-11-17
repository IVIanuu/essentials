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
import com.ivianuu.essentials.store.StoreFromSourceImpl.StoreMessage.Dispatch
import com.ivianuu.essentials.store.StoreFromSourceImpl.StoreMessage.Reduce
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.launch

interface Store<S, A> {
    val state: StateFlow<S>
    fun dispatch(action: A)
}

interface StoreScope<S, A> : CoroutineScope {
    val actions: Flow<A>
    val state: Flow<S>
    suspend fun reduce(block: S.() -> S): S
}

operator fun <S, A> StoreScope<S, A>.iterator(): StoreScopeActionIterator<S, A> {
    val channelIterator = actions.produceIn(this).iterator()
    return object : StoreScopeActionIterator<S, A> {
        override suspend fun hasNext(): Boolean = channelIterator.hasNext()
        override suspend fun next(): A = channelIterator.next()
    }
}

interface StoreScopeActionIterator<S, A> {
    suspend operator fun hasNext(): Boolean
    suspend operator fun next(): A
}

suspend inline fun <S, A> StoreScope<S, A>.currentState(): S = state.first()

suspend inline fun <S, A, T> Flow<T>.reduce(
    scope: StoreScope<S, A>,
    crossinline reducer: S.(T) -> S,
) {
    collect { value ->
        scope.reduce { reducer(value) }
    }
}

inline fun <S, A, T> Flow<T>.reduceIn(
    scope: StoreScope<S, A>,
    crossinline reducer: S.(T) -> S,
): Job = scope.launch { reduce(scope, reducer) }

suspend inline fun <S, A> StoreScope<S, A>.forEachAction(action: (A) -> Unit) {
    for (item in this) action(item)
}

suspend inline fun <S, A> StoreScope<S, A>.awaitAction(): A = iterator().next()

suspend inline fun <S, A> StoreScope<S, A>.reduceEachAction(crossinline reducer: S.(A) -> S) {
    forEachAction {
        reduce {
            reducer(it)
        }
    }
}

fun <S, A> CoroutineScope.reducerStore(
    initial: S,
    reducer: S.(A) -> S
): Store<S, A> = store(initial) {
    reduceEachAction { reducer(it) }
}

fun <S, A> CoroutineScope.simpleStore(
    initial: S,
    action: suspend StoreScope<S, A>.(A) -> Unit
): Store<S, A> = store(initial) {
    forEachAction { action(it) }
}

fun <S, A> CoroutineScope.store(
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit
): Store<S, A> {
    val state = MutableStateFlow(initial)
    return storeFromSource(
        state = state,
        setState = { state.value = it },
        block = block
    )
}

fun <S, A> CoroutineScope.storeFromSource(
    state: StateFlow<S>,
    setState: suspend (S) -> Unit,
    block: suspend StoreScope<S, A>.() -> Unit
): Store<S, A> = StoreFromSourceImpl(this, state, setState, block)

internal class StoreFromSourceImpl<S, A>(
    scope: CoroutineScope,
    override val state: StateFlow<S>,
    private val setState: suspend (S) -> Unit,
    block: suspend StoreScope<S, A>.() -> Unit
) : Store<S, A>, StoreScope<S, A>, CoroutineScope by scope {

    override val actions = EventFlow<A>()

    private val actor = actor<StoreMessage<S, A>>(capacity = Channel.UNLIMITED) {
        launch { block() }
        for (msg in channel) {
            @Suppress("IMPLICIT_CAST_TO_ANY")
            when (msg) {
                is Dispatch -> actions.emit(msg.action)
                is Reduce -> {
                    val currentState = state.value
                    val newState = msg.reducer(currentState)
                    if (currentState != newState) setState(newState)
                    msg.acknowledged.complete(newState)
                }
            }.let {}
        }
    }

    override fun dispatch(action: A) {
        actor.offer(Dispatch(action))
    }

    override suspend fun reduce(block: S.() -> S): S {
        val acknowledged = CompletableDeferred<S>()
        actor.offer(Reduce(acknowledged, block))
        return acknowledged.await()
    }

    sealed class StoreMessage<S, A> {
        data class Dispatch<S, A>(val action: A) : StoreMessage<S, A>()
        data class Reduce<S, A>(
            val acknowledged: CompletableDeferred<S>,
            val reducer: S.() -> S,
        ) : StoreMessage<S, A>()
    }
}
