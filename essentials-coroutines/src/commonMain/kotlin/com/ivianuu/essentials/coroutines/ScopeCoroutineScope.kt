package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.GivenScope
import com.ivianuu.injekt.scope.GivenScopeDisposable
import com.ivianuu.injekt.scope.GivenScopeElement
import com.ivianuu.injekt.scope.getOrCreateScopedValue
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

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

@Given
fun <S : GivenScope> scopeCoroutineScopeElement(
    @Given scope: () -> ScopeCoroutineScope<S>
): GivenScopeElement<S> = typeKeyOf<CoroutineScope>() to scope

val <T : GivenScope> T.coroutineScope: ScopeCoroutineScope<T>
    get() = element<CoroutineScope>()

typealias ScopeCoroutineDispatcher<S> = CoroutineDispatcher

@Given
fun <S : GivenScope> defaultScopeCoroutineDispatcher(
    @Given dispatcher: DefaultDispatcher
): ScopeCoroutineDispatcher<S> = dispatcher
