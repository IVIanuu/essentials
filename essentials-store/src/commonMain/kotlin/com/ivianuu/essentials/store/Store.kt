package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.stateStore
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.scope.GivenScope
import com.ivianuu.injekt.scope.Scoped
import com.ivianuu.injekt.scope.getOrCreateScopedValue
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

    fun effect(block: suspend () -> Unit): Job = launch { block() }

    fun Flow<S.() -> S>.update(): Job = this@StoreScope.effect {
        collect { update(it) }
    }

    fun <T> Flow<T>.update(reducer: S.(T) -> S): Job =
        map<T, S.() -> S> { { reducer(it) } }.update()

    fun <T> Flow<T>.effect(block: suspend (T) -> Unit): Job =
        onEach(block).launchIn(this@StoreScope)
}

inline fun <reified T> StoreScope<*, in T>.onAction(noinline block: suspend (T) -> Unit): Job =
    actions.filterIsInstance<T>().effect(block)

fun <S, A, R> Store<S, A>.mapState(transform: (S) -> R): Store<R, A> =
    object : Store<R, A>, Sink<A> by this, StateFlow<R> by mapState(transform) {
    }

fun <S, A, R> Store<S, A>.mapSink(transform: (R) -> A): Store<S, R> =
    object : Store<S, R>, Sink<R> by (Sink { send(transform(it)) }), StateFlow<S> by this {
    }

fun <S, A> StoreBuilder<*, S, A>.toStore(
    scope: CoroutineScope,
    initial: S,
    actions: MutableSharedFlow<A> = EventFlow()
): Store<S, A> = scope.store(initial, actions, this)

@Given
class StoreFactory<GS : GivenScope, S, A>(
    @Given private val initialFactory: () -> @InitialOrFallback S,
    @Given private val actionsFactory: () -> @ActionsOrFallback<GS> MutableSharedFlow<A>,
    @Given private val scope: ScopeCoroutineScope<GS>
) {
    operator fun invoke(
        initial: S = initialFactory(),
        actions: MutableSharedFlow<A> = actionsFactory(),
        builder: StoreBuilder<GS, S, A>
    ): Store<S, A> = scope.store(initial, actions, builder)
}

@Qualifier
annotation class ActionsOrFallback<S : GivenScope>

@Given
fun <S : GivenScope, A> actionsOrFallback(
    @Given actions: MutableSharedFlow<A>? = null,
    @Given actionKey: TypeKey<A>,
    @Given scope: S
): @ActionsOrFallback<S> MutableSharedFlow<A> = actions ?: scope.getOrCreateScopedValue(actionKey) {
    EventFlow()
}

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
