package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface StateStore<T> : StateFlow<T> {
    suspend fun update(transform: T.() -> T): T
    fun dispatchUpdate(transform: T.() -> T)
}

fun <T> CoroutineScope.stateStore(initial: T): StateStore<T> =
    StateStoreImpl(this, initial)

private class StateStoreImpl<T>(
    private val scope: CoroutineScope,
    initial: T,
    private val state: MutableStateFlow<T> = MutableStateFlow(initial)
) : StateStore<T>, StateFlow<T> by state {
    private val mutex = Mutex()
    override suspend fun update(transform: T.() -> T): T = mutex.withLock {
        val currentState = state.value
        val newState = transform(currentState)
        state.value = newState
        newState
    }
    override fun dispatchUpdate(transform: T.() -> T) {
        scope.launch { update(transform) }
    }
}
