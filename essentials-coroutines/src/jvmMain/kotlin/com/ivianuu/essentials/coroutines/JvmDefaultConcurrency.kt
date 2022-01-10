/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.*

@Provide actual val defaultConcurrency by lazy(LazyThreadSafetyMode.NONE) {
  Concurrency(Runtime.getRuntime().availableProcessors().coerceAtLeast(3))
}
