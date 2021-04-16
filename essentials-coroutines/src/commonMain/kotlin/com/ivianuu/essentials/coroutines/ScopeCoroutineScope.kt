package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

typealias ScopeCoroutineScope<S> = CoroutineScope

@Given
fun <S : GivenScope> scopeCoroutineScope(
    @Given scope: S,
    @Given dispatcher: ScopeCoroutineDispatcher<S>,
    @Given typeKey: TypeKey<ScopeCoroutineScope<S>>
): ScopeCoroutineScope<S> = scope.getOrCreateScopedValue(typeKey) {
    object : CoroutineScope, GivenScopeDisposable {
        override val coroutineContext: CoroutineContext = SupervisorJob() + dispatcher
        override fun dispose() {
            coroutineContext.cancel()
        }
    }
}

typealias ScopeCoroutineDispatcher<S> = CoroutineDispatcher

@Given
fun <S : GivenScope> defaultScopeCoroutineDispatcher(
    @Given dispatcher: DefaultDispatcher
): ScopeCoroutineDispatcher<S> = dispatcher
