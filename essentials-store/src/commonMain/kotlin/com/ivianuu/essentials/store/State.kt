package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.optics.Lens
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
}

suspend fun <S> StateScope<S>.action(
    lens: Lens<S, () -> Unit>,
    block: suspend () -> Unit
) {
    val callback: () -> Unit = { launch { block() } }
    update { lens.set(this, callback) }
}

suspend fun <S, P1> StateScope<S>.action(
    lens: Lens<S, (P1) -> Unit>,
    block: suspend (P1) -> Unit
) {
    val callback: (P1) -> Unit = { p1 -> launch { block(p1) } }
    update { lens.set(this, callback) }
}

suspend fun <S, P1, P2> StateScope<S>.action(
    lens: Lens<S, (P1, P2) -> Unit>,
    block: suspend (P1, P2) -> Unit
) {
    val callback: (P1, P2) -> Unit = { p1, p2 -> launch { block(p1, p2) } }
    update { lens.set(this, callback) }
}

suspend fun <S, P1, P2, P3> StateScope<S>.action(
    lens: Lens<S, (P1, P2, P3) -> Unit>,
    block: suspend (P1, P2, P3) -> Unit
) {
    val callback: (P1, P2, P3) -> Unit = { p1, p2, p3 -> launch { block(p1, p2, p3) } }
    update { lens.set(this, callback) }
}

suspend fun <S, P1, P2, P3, P4> StateScope<S>.action(
    lens: Lens<S, (P1, P2, P3, P4) -> Unit>,
    block: suspend (P1, P2, P3, P4) -> Unit
) {
    val callback: (P1, P2, P3, P4) -> Unit = { p1, p2, p3, p4 -> launch { block(p1, p2, p3, p4) } }
    update { lens.set(this, callback) }
}

suspend fun <S, P1, P2, P3, P4, P5> StateScope<S>.action(
    lens: Lens<S, (P1, P2, P3, P4, P5) -> Unit>,
    block: suspend (P1, P2, P3, P4, P5) -> Unit
) {
    val callback: (P1, P2, P3, P4, P5) -> Unit = { p1, p2, p3, p4, p5 -> launch { block(p1, p2, p3, p4, p5) } }
    update { lens.set(this, callback) }
}

fun <S> Flow<S.() -> S>.updateIn(scope: StateScope<S>): Job = scope.launch {
    collect { scope.update(it) }
}

fun <T, S> Flow<T>.updateIn(scope: StateScope<S>, transform: S.(T) -> S): Job =
    map<T, S.() -> S> { { transform(it) } }.updateIn(scope)

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
