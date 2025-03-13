/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.coroutines

import androidx.compose.runtime.*
import essentials.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

@Stable class ScopedCoroutineScope<N> @Provide @Scoped<N> constructor(
  context: ScopeCoroutineContext<N>
): CoroutineScope, DisposableHandle {
  override val coroutineContext: CoroutineContext = context + SupervisorJob()
  override fun dispose() {
    coroutineContext.cancel()
  }

  @Provide companion object {
    @Provide fun <N> service(scope: () -> ScopedCoroutineScope<N>) =
      ProvidedService<N, CoroutineScope>(CoroutineScope::class, scope)

    @Provide fun coroutinesScope(scope: ScopedCoroutineScope<*>): CoroutineScope = scope
  }
}

@Tag typealias ScopeCoroutineContext<N> = CoroutineContext

@Provide fun <N> scopeCoroutineContext(
  contexts: CoroutineContexts
): ScopeCoroutineContext<N> = contexts.main
