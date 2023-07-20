/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.Disposable
import com.ivianuu.essentials.ProvidedService
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Scoped
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

typealias ScopedCoroutineScope<N> = @ScopedCoroutineScopeTag<N> CoroutineScope

@Tag annotation class ScopedCoroutineScopeTag<N> {
  @Provide companion object {
    @Provide fun <N> scope(
      context: ScopeCoroutineContext<N>
    ): @Scoped<N> ScopedCoroutineScope<N> = object : CoroutineScope, Disposable {
      override val coroutineContext: CoroutineContext = context + SupervisorJob()
      override fun dispose() {
        coroutineContext.cancel()
      }
    }

    @Provide fun <N> service(scope: () -> ScopedCoroutineScope<N>) =
      ProvidedService<N, CoroutineScope>(typeKeyOf(), scope)
  }
}

typealias ScopeCoroutineContext<N> = @ScopeCoroutineContextTag<N> CoroutineContext

@Tag annotation class ScopeCoroutineContextTag<N> {
  @Provide companion object {
    @Provide fun <N> context(contexts: CoroutineContexts): ScopeCoroutineContext<N> = contexts.main
  }
}

val Scope<*>.coroutineScope: CoroutineScope get() = service()
