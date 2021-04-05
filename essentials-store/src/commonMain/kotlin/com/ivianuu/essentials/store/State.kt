package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.runOnCancellation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun <S> CoroutineScope.state(
    initial: S,
    block: suspend StateScope<S>.() -> Unit
): StateFlow<S> {
    val state = MutableStateFlow(initial)
    val stateScope = object : StateScope<S>, CoroutineScope by this {
        override val state: Flow<S>
            get() = state
        private val mutex = Mutex()
        override suspend fun update(reducer: S.() -> S): S = mutex.withLock {
            val currentState = state.value
            val newState = reducer(currentState)
            if (currentState != newState) state.value = newState
            newState
        }
    }
    stateScope.launch(start = CoroutineStart.UNDISPATCHED) { stateScope.block() }
    return state
}

interface StateScope<S> : CoroutineScope {
    val state: Flow<S>
    suspend fun update(reducer: S.() -> S): S
}

fun <T> Flow<T>.collectIn(scope: CoroutineScope, action: suspend (T) -> Unit): Job =
    onEach(action).launchIn(scope)

fun <T, S> Flow<T>.updateIn(scope: StateScope<S>, reducer: S.(T) -> S): Job =
    onEach { scope.update { reducer(it) } }.launchIn(scope)

object CancellableUpdatesScope {
    fun onCancel(block: () -> Unit) = block
}

fun <S> StateScope<S>.cancellableUpdates(
    block: CancellableUpdatesScope.((S.() -> S) -> Unit) -> () -> Unit
): Job = launch {
    val cleanUp = with(CancellableUpdatesScope) {
        block { reducer ->
            launch {
                update(reducer)
            }
        }
    }
    runOnCancellation { cleanUp() }
}
