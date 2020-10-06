package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.EventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

interface Store<S, A> {
    val state: StateFlow<S>
    fun dispatch(action: A)
}

interface StoreScope<S, A> : CoroutineScope {
    val state: MutableStateFlow<S>
    val actions: Flow<A>
}

suspend fun <S, A> StoreScope<S, A>.onEachAction(block: suspend (A) -> Unit) {
    actions.collect(block)
}

fun <S, A> StoreScope<S, A>.setState(reducer: suspend S.() -> S) {
    launch {
        val newState = reducer(state.value)
        state.value = newState
    }
}

var <S, A> StoreScope<S, A>.currentState: S
    get() = state.value
    set(value) {
        state.value = value
    }

@BuilderInference
fun <S, A> CoroutineScope.store(
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit,
): Store<S, A> = StoreImpl(this, initial, block)

@BuilderInference
fun <S, A> storeProvider(
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit,
): (CoroutineScope) -> Store<S, A> = {
    it.store(initial, block)
}

internal class StoreImpl<S, A>(
    private val scope: CoroutineScope,
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit,
) : Store<S, A>, StoreScope<S, A>, CoroutineScope by scope {

    override val state = MutableStateFlow(initial)
    override val actions = EventFlow<A>()

    init {
        launch {
            block()
        }
    }

    override fun dispatch(action: A) {
        actions.offer(action)
    }

}
