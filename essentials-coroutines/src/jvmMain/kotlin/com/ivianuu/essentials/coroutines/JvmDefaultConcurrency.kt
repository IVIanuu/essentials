package com.ivianuu.essentials.coroutines

actual val defaultConcurrency by lazy(LazyThreadSafetyMode.NONE) {
    Runtime.getRuntime().availableProcessors().coerceAtLeast(3)
}
