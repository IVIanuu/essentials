/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.Scoped
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

@Stable class ScopedCoroutineScope<N> @Provide @Scoped<N> constructor(
  context: @ScopeCoroutineContextTag<N> CoroutineContext
): CoroutineScope, Disposable {
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

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class ScopeCoroutineContextTag<N> {
  @Provide companion object {
    @Provide fun <N> context(
      contexts: CoroutineContexts
    ): @ScopeCoroutineContextTag<N> CoroutineContext = contexts.main
  }
}
