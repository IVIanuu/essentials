package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.scope.GivenScope
import com.ivianuu.injekt.scope.GivenScopeDisposable
import com.ivianuu.injekt.scope.getOrCreateScopedValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

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
