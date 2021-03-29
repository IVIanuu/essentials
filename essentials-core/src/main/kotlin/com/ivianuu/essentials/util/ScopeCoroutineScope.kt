package com.ivianuu.essentials.util

import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.ForTypeKey
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
fun <@ForTypeKey S : GivenScope> scopeCoroutineScope(
    @Given scope: S,
    @Given dispatcher: ScopeCoroutineDispatcher<S>
): ScopeCoroutineScope<S> = scope.getOrCreateScopedValue {
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
