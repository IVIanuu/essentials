/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Provide

@Provide actual object ConcurrencyModule {
  @Provide actual val defaultConcurrencyContext: ConcurrencyContext by lazy {
    ConcurrencyContext(Runtime.getRuntime().availableProcessors().coerceAtLeast(3))
  }
}
