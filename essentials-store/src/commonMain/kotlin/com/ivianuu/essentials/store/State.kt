package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.GivenScope
import com.ivianuu.injekt.scope.Scoped
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

    fun effect(block: suspend () -> Unit): Job = launch { block() }

    fun Flow<S.() -> S>.update(): Job = this@StateScope.effect {
        collect { update(it) }
    }

    fun <T> Flow<T>.update(reducer: S.(T) -> S): Job =
        map<T, S.() -> S> { { reducer(it) } }.update()

    fun <T> Flow<T>.effect(block: suspend (T) -> Unit): Job =
        onEach(block).launchIn(this@StateScope)
}

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

fun <S> StateBuilder<*, S>.toState(
    scope: CoroutineScope,
    initial: S
): StateFlow<S> = scope.state(initial, this)

typealias StateBuilder<GS, S> = suspend StateScope<S>.() -> Unit

@Given
fun <@Given T : StateBuilder<GS, S>, GS : GivenScope, S> state(
    @Given builder: T,
    @Given initial: @InitialOrFallback S,
    @Given scope: ScopeCoroutineScope<GS>
): @Scoped<GS> StateFlow<S> = scope.state(initial, builder)
