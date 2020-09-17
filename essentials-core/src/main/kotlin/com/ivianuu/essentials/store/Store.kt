package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.awaitCancellation
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface Store<S, A> {
    val state: StateFlow<S>
    fun dispatch(action: A)
}

interface StoreScope<S, A> {
    val state: MutableStateFlow<S>
    val actions: Flow<A>
    val scope: CoroutineScope
}

@Reader
inline fun <S, A> StoreScope<S, A>.enableLogging() {
    d { "initialize with state $currentState" }

    state
        .drop(1)
        .onEach { d { "new state -> $it" } }
        .launchIn(scope)

    actions
        .onEach { d { "on action -> $it" } }
        .launchIn(scope)

    scope.launch {
        runWithCleanup(
            block = { awaitCancellation() },
            cleanup = { d { "cancel" } }
        )
    }
}

suspend fun <S, A> StoreScope<S, A>.onEachAction(block: suspend (A) -> Unit) {
    actions.collect(block)
}

fun <S, A> StoreScope<S, A>.setState(reducer: suspend S.() -> S) {
    scope.launch {
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
fun <S, A> CoroutineScope.baseStore(
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit
): Store<S, A> = StoreImpl(this, initial, block)

@Reader
@BuilderInference
inline fun <S, A> CoroutineScope.store(
    initial: S,
    noinline block: suspend StoreScope<S, A>.() -> Unit
): Store<S, A> = baseStore(initial) {
    enableLogging()
    block()
}

internal class StoreImpl<S, A>(
    override val scope: CoroutineScope,
    initial: S,
    block: suspend StoreScope<S, A>.() -> Unit
) : Store<S, A>, StoreScope<S, A> {

    override val state = MutableStateFlow(initial)
    override val actions = EventFlow<A>()

    init {
        scope.launch {
            block()
        }
    }

    override fun dispatch(action: A) {
        actions.offer(action)
    }

}
