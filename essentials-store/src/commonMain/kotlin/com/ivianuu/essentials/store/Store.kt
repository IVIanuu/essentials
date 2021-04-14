package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.stateStore
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.GivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface Store<S, A> : StateFlow<S>, Sink<A>

fun <S, A> CoroutineScope.store(
    initial: S,
    actions: MutableSharedFlow<A> = EventFlow(),
    block: suspend StoreScope<S, A>.() -> Unit
): Store<S, A> {
    val state = stateStore(initial)
    val store = object : Store<S, A>, StoreScope<S, A>, StateFlow<S> by state, CoroutineScope by this {
        override val actions: Flow<A>
            get() = actions
        override val state: Flow<S>
            get() = state
        override suspend fun update(reducer: S.() -> S): S = state.update(reducer)
        override fun send(value: A) {
            actions.tryEmit(value)
        }
    }
    store.launch(start = CoroutineStart.UNDISPATCHED) { store.block() }
    return store
}

interface StoreScope<S, A> : CoroutineScope {
    val state: Flow<S>
    val actions: Flow<A>
    suspend fun update(reducer: S.() -> S): S
}

inline fun <reified T> StoreScope<*, in T>.actions(): Flow<T> = actions.filterIsInstance()

fun <S> Flow<S.() -> S>.updateIn(scope: StoreScope<S, *>): Job = scope.launch {
    collect { scope.update(it) }
}

fun <T, S> Flow<T>.updateIn(scope: StoreScope<S, *>, reducer: S.(T) -> S): Job =
    map<T, S.() -> S> { { reducer(it) } }.updateIn(scope)

fun <S, A> StoreBuilder<*, S, A>.toStore(
    scope: CoroutineScope,
    initial: S,
    actions: MutableSharedFlow<A> = EventFlow()
): Store<S, A> = scope.store(initial, actions, this)

typealias StoreBuilder<GS, S, A> = suspend StoreScope<S, A>.() -> Unit

@Given
class StoreBuilderModule<@Given T : StoreBuilder<GS, S, A>, GS : GivenScope, S, A> {
    @Given
    fun store(
        @Given builder: T,
        @Given initial: @InitialOrFallback S,
        @Given actions: MutableSharedFlow<A>,
        @Given scope: ScopeCoroutineScope<GS>
    ): @Scoped<GS> Store<S, A> = scope.store(initial, actions, builder)

    @Given
    val actions: @Scoped<GS> MutableSharedFlow<A>
        get() = EventFlow()
}
