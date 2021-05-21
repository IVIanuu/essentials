package com.ivianuu.essentials.coroutines

@PublishedApi internal expect inline fun <T> synchronized(lock: Any, block: () -> T): T
