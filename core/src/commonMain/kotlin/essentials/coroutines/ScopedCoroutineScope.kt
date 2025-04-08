/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.coroutines

import androidx.compose.runtime.*
import essentials.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

@Provide object CoroutineProviders : BaseCoroutineProviders() {
  @Provide fun <N> scopedCoroutineScope(
    context: @For<N> CoroutineContext
  ): @Scoped<N> @For<N> CoroutineScope =
    object : CoroutineScope by CoroutineScope(context), DisposableHandle {
      override fun dispose() {
        cancel()
      }
    }

  @Provide fun <N> scopeCoroutineContext(
    contexts: CoroutineContexts
  ): @For<N> CoroutineContext = contexts.main

  @Provide fun <N> coroutineScopeService(scope: () -> @For<N> CoroutineScope) =
    ProvidedService<N, CoroutineScope>(CoroutineScope::class, scope)

  @Provide fun scopeCoroutineScope(scope: Scope<*>): CoroutineScope = scope.service()
}

abstract class BaseCoroutineProviders {
  @Provide @Composable fun compositionCoroutineScope(): CoroutineScope = rememberCoroutineScope()
}
