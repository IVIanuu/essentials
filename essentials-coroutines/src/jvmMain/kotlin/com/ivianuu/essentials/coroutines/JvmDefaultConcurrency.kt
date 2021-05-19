package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.*

@Provide actual val defaultConcurrency by lazy(LazyThreadSafetyMode.NONE) {
  Concurrency(Runtime.getRuntime().availableProcessors().coerceAtLeast(3))
}
