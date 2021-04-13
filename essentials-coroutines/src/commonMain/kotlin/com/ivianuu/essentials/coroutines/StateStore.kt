package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

interface StateStore<T> {
    val state: StateFlow<T>
    suspend fun update(reducer: T.() -> T): T
    fun effect(block: suspend StateStore<T>.() -> Unit)
}

fun <T> CoroutineScope.stateStore(initial: T): StateStore<T> =
    StateStoreImpl(this, initial)

private class StateStoreImpl<T>(
    private val scope: CoroutineScope,
    initial: T,
    override val state: MutableStateFlow<T> = MutableStateFlow(initial)
) : StateStore<T> {
    private val actor = scope.actor(capacity = Channel.UNLIMITED)
    override suspend fun update(reducer: T.() -> T): T = actor.actAndReply {
        val currentState = state.value
        val newState = reducer(currentState)
        state.value = newState
        newState
    }

    override fun effect(block: suspend StateStore<T>.() -> Unit) {
        scope.launch { block() }
    }
}

fun <T> StateStore<T>.dispatchUpdate(reducer: T.() -> T) = effect {
    update(reducer)
}

suspend fun <T, S> Flow<T>.update(store: StateStore<S>, reducer: S.(T) -> S) {
    collect { store.update { reducer(it) } }
}

fun <T, S> Flow<T>.updateIn(store: StateStore<S>, reducer: S.(T) -> S) = store.effect {
    this@updateIn.update(store, reducer)
}

fun <T, S> Flow<T>.collectAsEffectIn(
    store: StateStore<S>,
    collector: suspend (T) -> Unit = {}
) = store.effect { collect(collector) }
