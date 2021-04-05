package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.GivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull

interface Feature<S, A> : StateFlow<S>, Collector<A>

fun <S, A> CoroutineScope.feature(
    initial: S,
    block: suspend FeatureScope<S, A>.() -> Unit
): Feature<S, A> {
    val actions = EventFlow<A>()
    val state = state(initial) {
        object : FeatureScope<S, A>, StateScope<S> by this {
            override val actions: Flow<A>
                get() = actions
        }.block()
    }
    return object : Feature<S, A>, StateFlow<S> by state, Collector<A> {
        override suspend fun emit(value: A) = actions.emit(value)
        override fun tryEmit(action: A): Boolean = actions.tryEmit(action)
    }
}

interface FeatureScope<S, A> : StateScope<S> {
    val actions: Flow<A>
}

inline fun <reified T> FeatureScope<*, in T>.actionsOf(): Flow<T> = actions.filterIsInstance()

typealias FeatureBuilder<GS, S, A> = suspend FeatureScope<S, A>.() -> Unit

@Given
fun <@Given T : FeatureBuilder<GS, S, A>, GS : GivenScope, S, A> feature(
    @Given builder: T,
    @Given initial: @InitialOrFallback S,
    @Given scope: ScopeCoroutineScope<GS>
): @Scoped<GS> Feature<S, A> = builder.toFeature(scope, initial)

fun <S, A> FeatureBuilder<*, S, A>.toFeature(
    scope: CoroutineScope,
    initial: S
): Feature<S, A> = scope.feature(initial, this)
