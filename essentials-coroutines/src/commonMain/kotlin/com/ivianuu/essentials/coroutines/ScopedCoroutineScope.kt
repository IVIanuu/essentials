/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.DefaultCoroutineContext
import com.ivianuu.injekt.common.Disposable
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

typealias ScopedCoroutineScope<N> = @ScopedCoroutineScopeTag<N> CoroutineScope

@Tag annotation class ScopedCoroutineScopeTag<N> {
  companion object {
    @Provide fun <N> scope(
      context: ScopeCoroutineContext<N>
    ): @Scoped<N> ScopedCoroutineScope<N> = object : CoroutineScope, Disposable {
      override val coroutineContext: CoroutineContext = context + SupervisorJob()
      override fun dispose() {
        coroutineContext.cancel()
      }
    }
  }
}

typealias ScopeCoroutineContext<N> = @ScopeCoroutineContextTag<N> CoroutineContext

@Tag annotation class ScopeCoroutineContextTag<N> {
  companion object {
    @Provide inline fun <N> context(context: DefaultCoroutineContext): ScopeCoroutineContext<N> = context
  }
}