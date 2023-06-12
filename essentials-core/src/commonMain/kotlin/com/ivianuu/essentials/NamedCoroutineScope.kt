/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Disposable
import com.ivianuu.injekt.common.NamedCoroutineContext
import com.ivianuu.injekt.common.NamedCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

object NamedCoroutineScopeModule {
  @Provide fun <N> instance(
    context: NamedCoroutineContext<N>
  ): @Scoped<N> NamedCoroutineScope<N> = object : CoroutineScope, Disposable {
    override val coroutineContext: CoroutineContext = context + SupervisorJob()
    override fun dispose() {
      coroutineContext.cancel()
    }
  }
}
