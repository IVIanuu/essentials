package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.coroutines.StateStore
import com.ivianuu.essentials.coroutines.stateStore
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.scope.GivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlin.reflect.KClass

@Qualifier
annotation class InitialOrFallback

@Given
inline fun <reified T : Any> initialOrFallback(
    @Given initial: @Initial T? = null
): @InitialOrFallback T = initial ?: T::class.newInstance()

typealias ScopeStateStore<S, T> = StateStore<T>

typealias State = Any

@Given
fun <S : GivenScope, T : State> scopeStateStore(
    @Given scope: ScopeCoroutineScope<S>,
    @Given initial: @InitialOrFallback T
): @Scoped<S> ScopeStateStore<S, T> = scope.stateStore(initial)

expect fun <T : Any> KClass<T>.newInstance(): T
