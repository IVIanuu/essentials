package com.ivianuu.essentials.util

import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.Scope
import com.ivianuu.injekt.common.ScopeDisposable
import com.ivianuu.injekt.common.invoke
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

typealias ScopeCoroutineScope<S> = CoroutineScope

@Given
fun <@ForTypeKey S : Scope> scopeCoroutineScope(
    @Given scope: S,
    @Given dispatcher: ScopeCoroutineDispatcher<S>
): ScopeCoroutineScope<S> = scope {
    object : CoroutineScope, ScopeDisposable {
        override val coroutineContext: CoroutineContext = Job() + dispatcher
        override fun dispose() {
            coroutineContext.cancel()
        }
    }
}

typealias ScopeCoroutineDispatcher<S> = CoroutineDispatcher

@Given
fun <S : Scope> defaultScopeCoroutineDispatcher(
    @Given dispatcher: DefaultDispatcher
): ScopeCoroutineDispatcher<S> = dispatcher
